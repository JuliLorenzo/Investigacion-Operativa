package invop.services;

import invop.controllers.VentaDetalleController;
import invop.dto.ModificarArticuloDTO;
import invop.entities.*;
import invop.enums.ModeloInventario;
import invop.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.util.*;
import java.lang.Math;

@Service
public class ArticuloServiceImpl extends BaseServiceImpl<Articulo, Long> implements ArticuloService {

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private OrdenCompraService ordenCompraService;
    @Autowired
    private DemandaHistoricaService demandaHistoricaService;
    @Autowired
    private ProveedorArticuloService proveedorArticuloService;
    @Autowired
    private ProveedorService proveedorService;


    public ArticuloServiceImpl(BaseRepository<Articulo, Long> baseRepository, ArticuloRepository articuloRepository,
                               OrdenCompraService ordenCompraService, DemandaHistoricaService demandaHistoricaService,
                               ProveedorArticuloService proveedorArticuloService, ProveedorService proveedorService) {
        super(baseRepository);
        this.articuloRepository = articuloRepository;
        this.ordenCompraService = ordenCompraService;
        this.demandaHistoricaService = demandaHistoricaService;
        this.proveedorArticuloService = proveedorArticuloService;
        this.proveedorService = proveedorService;
    }

    public Articulo findArticuloById(Long id) {
        return articuloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado con id: " + id));
    }

    //Controla que el Articulo no tenga Ordenes de Compras Activas.
    public boolean controlOrdenCompraActiva(Long idArticulo) throws Exception{
            boolean ordenActiva = ordenCompraService.articuloConOrdenActiva(idArticulo);
            return ordenActiva;
    }


    public boolean darDeBajaArticulo(Long idArticulo) throws Exception{
        boolean ordenActiva = controlOrdenCompraActiva(idArticulo);
        try {
            if (!ordenActiva) {
                Articulo articuloABorrar = articuloRepository.findById(idArticulo).orElseThrow(() -> new EntityNotFoundException("Articulo no encontrado"));
                articuloRepository.deleteDetallesByVenta(idArticulo);
                articuloRepository.deleteDetallesPorArticulo(idArticulo);
                articuloRepository.deletePrediccionesByArticulo(idArticulo);
                articuloRepository.deleteDemandasHistoricasPorArticulo(idArticulo);
                articuloRepository.deleteProveedorArticuloByArticulo(idArticulo);
                articuloRepository.deleteErroresByArticulo(idArticulo);
                articuloRepository.delete(articuloABorrar);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<Long> getArticulosSinStock(Map<String, Integer> articulosDetalleVenta){
        List<Long> articulosSinStock = new ArrayList<>();

        for (Map.Entry<String,Integer> item : articulosDetalleVenta.entrySet()) {
            String idArticuloStr = item.getKey();
            Long idArticulo = Long.parseLong(idArticuloStr);
            Integer cantidad = item.getValue();

            if (articuloRepository.getById(idArticulo).getCantidadArticulo() < cantidad) {
                articulosSinStock.add(idArticulo);
            }
        }
        return articulosSinStock;
    }
    public void disminuirStock(Articulo articulo, Integer cantVendida) throws Exception{
        try {
            Integer nuevoStock = articulo.getCantidadArticulo() - cantVendida;
            articulo.setCantidadArticulo(nuevoStock);
            articuloRepository.save(articulo);

            if (articulo.getModeloInventario() == ModeloInventario.MODELO_LOTE_FIJO) {
                controlStockPP(articulo);
            }
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }
    @Override
    @Transactional
    public void aumentarStock(Articulo articulo, Integer cantPedida){
        Integer nuevoStock = articulo.getCantidadArticulo() + cantPedida;
        articulo.setCantidadArticulo(nuevoStock);
        articuloRepository.save(articulo);

    }

    public void controlStockPP(Articulo articulo) throws Exception{
        if (articulo.getCantidadArticulo() <= articulo.getPuntoPedidoArticulo()){
            try{
                if (!controlOrdenCompraActiva(articulo.getId())){
                    ordenCompraService.crearOrdenCompraAutomatica(articulo);
                }

            }catch (Exception e){
                throw new Exception(e.getMessage());

            }
        }
    }
    @Override
    public Double calculoCGI(Long idArticulo) throws Exception {
        try {
            Articulo articulo = articuloRepository.findById(idArticulo).orElseThrow(() -> new Exception("Articulo no encontrado"));
            Double precioArticulo = proveedorArticuloService.findPrecioArticuloByArticuloAndProveedor(idArticulo, articulo.getProveedorPredeterminado().getId());
            Integer cantidadAComprar = 0;

            if (articulo.getModeloInventario() == ModeloInventario.MODELO_LOTE_FIJO) {
                cantidadAComprar = articulo.getLoteOptimoArticulo();
                System.out.println("Cantidad a comprar:" + cantidadAComprar);
            } else {
                cantidadAComprar = cantidadAPedir(articulo);
                System.out.println("Cantidad a comprar:" + cantidadAComprar);

            }
            double costoCompra = precioArticulo * articulo.getDemandaAnualArticulo();

            System.out.println("Costo de Compra: " + costoCompra);

            Double CGI = costoCompra + articulo.getCostoAlmacenamientoArticulo() * (cantidadAComprar / 2) + articulo.getCostoPedidoArticulo() * (articulo.getDemandaAnualArticulo() / cantidadAComprar);
            System.out.println("CGI: " + CGI);

            return CGI;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void guardarValorCGI(Double valorCGI, Articulo Articulo) throws Exception{
        Articulo.setCgiArticulo(valorCGI);
        articuloRepository.save(Articulo);
    }

    // METODOS LOTE FIJO:
    @Override
    public void calculosModeloLoteFijo(Long idArticulo) throws Exception{
        try{
            Articulo articulo = articuloRepository.findById(idArticulo).orElseThrow(() -> new Exception("Articulo no encontrado"));
            Integer loteOptimo = calculoDeLoteOptimo(idArticulo);
            int puntoPedido = calculoPuntoPedido(idArticulo);
            int stockSeguridad = calculoStockSeguridad(idArticulo);
            puntoPedido += stockSeguridad;
            articulo.setLoteOptimoArticulo(loteOptimo);
            articulo.setPuntoPedidoArticulo(puntoPedido);
            articulo.setStockSeguridadArticulo(stockSeguridad);
            articuloRepository.save(articulo);
            Double cgi = calculoCGI(idArticulo);

            articulo.setCgiArticulo(cgi);
            articuloRepository.save(articulo);

        }catch (Exception e ){
            throw new Exception(e.getMessage());
        }
    }
    @Override
    //@Transactional
    public void calculosModeloIntervaloFijo(Long idArticulo) throws Exception {
        try {
            Articulo articulo = articuloRepository.findById(idArticulo).orElseThrow(() -> new Exception("Articulo no encontrado"));

            Integer stockSeguridad = calculoStockSeguridad(idArticulo);
            Integer cantidadMaxima = cantidadMaxima(articulo);
            Double cgi = calculoCGI(idArticulo);

            articulo.setStockSeguridadArticulo(stockSeguridad);
            articulo.setCantidadMaximaArticulo(cantidadMaxima);
            articulo.setCgiArticulo(cgi);
            articuloRepository.save(articulo);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    @Override
    @Transactional
    public Integer calculoDemandaAnual(Long idArticulo) throws Exception {
        try {
            // Obtener fecha actual y fecha de hace un año
            LocalDate fechaActual = LocalDate.now();
            LocalDate fechaHaceUnAno = fechaActual.minusYears(1);
            Integer demandaAnual = demandaHistoricaService.calcularDemandaHistorica(fechaHaceUnAno,fechaActual,idArticulo);
            Articulo articulo = articuloRepository.findById(idArticulo).orElseThrow(() -> new Exception("Articulo no encontrado"));

            // esto es para que si el artículo es nuevo, usa el atributo cargado en articulo como la demanda anual
            if (demandaAnual == 0) {
                demandaAnual = articulo.getDemandaAnualArticulo();
            }

            articulo.setDemandaAnualArticulo(demandaAnual);
            articuloRepository.save(articulo);
            return demandaAnual;
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public int calculoDeLoteOptimo(Long idArticulo) throws Exception {
        try{
            Articulo articulo = findArticuloById(idArticulo);

            int demandaAnual = calculoDemandaAnual(idArticulo);
            Double costoAlmacenamiento = articulo.getCostoAlmacenamientoArticulo();
            Double costoPedido = articulo.getCostoPedidoArticulo();

            int loteOptimo = (int)Math.sqrt((2 * demandaAnual * costoPedido) / costoAlmacenamiento);
            return loteOptimo;
        }
        catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }


    public int calculoPuntoPedido(Long idArticulo) throws Exception{
        try {
            Articulo articulo = findArticuloById(idArticulo);
            int demandaAnual = calculoDemandaAnual(idArticulo);
            Double tiempoProveedor = proveedorArticuloService.findProveedorArticuloByAmbosIds(articulo.getId(), articulo.getProveedorPredeterminado().getId()).getTiempoDemoraArticulo();
            double demandaDiaria = (double)demandaAnual/365;
            int puntoPedido = (int)Math.ceil(demandaDiaria * tiempoProveedor);
            System.out.println(puntoPedido);

            return puntoPedido;

        } catch(Exception e ){
            throw new Exception(e.getMessage());
        }

    }
    public void guardarPuntoPedido(Integer valorPP, Articulo Articulo) throws Exception {
        Articulo.setPuntoPedidoArticulo(valorPP);
        articuloRepository.save(Articulo);

    }

    @Override
    public int calculoStockSeguridad(Long idArticulo) throws Exception{
        try {
            // Para unificar, usamos este método para SS de Lote Fijo y de Intervalo Fijo
            Articulo articulo = findArticuloById(idArticulo);
            double valorNormalZ = 1.64;
            Double tiempoProveedor = proveedorArticuloService.findProveedorArticuloByAmbosIds(articulo.getId(), articulo.getProveedorPredeterminado().getId()).getTiempoDemoraArticulo();
            Double tiempoRevision = articulo.getTiempoRevisionArticulo();

            // Si el artículo usa modelo de Lote Fijo, el tiempo de revisión o entre pedidos es null, por lo que lo tomamos como 0 para que no dé error
            if (tiempoRevision == null) {
                tiempoRevision = 0.0;
            }
            int stockSeguridad = (int) Math.ceil(valorNormalZ * Math.sqrt(tiempoRevision + tiempoProveedor));

            return stockSeguridad;
        }catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }
    public void guardarStockSeguridad(Integer valorSS, Articulo Articulo) throws Exception{
        Articulo.setStockSeguridadArticulo(valorSS);
        articuloRepository.save(Articulo);
    }

    //METODOS PARA EL MODELO INTERVALO FIJO
    @Override
    //@Transactional
    public Integer cantidadMaxima(Articulo articulo) throws Exception {
        try {
            Long idArticulo = articulo.getId();

            int demandaAnual = calculoDemandaAnual(idArticulo);
            Double tiempoEntrePedidos = articulo.getTiempoRevisionArticulo();
            Double tiempoDemoraProv = proveedorArticuloService.findProveedorArticuloByAmbosIds(articulo.getId(), articulo.getProveedorPredeterminado().getId()).getTiempoDemoraArticulo();
            Double valorNormalZ = 1.64;
            int desvEstandarDemandaDiaria = 1;

            Double desvEstandarTiempoPedidoYDemora = Math.sqrt(tiempoEntrePedidos + tiempoDemoraProv) * desvEstandarDemandaDiaria;

            double demandaPromedioDiaria = (double)demandaAnual/365;

            Integer cantidadMaxima = (int) Math.ceil(demandaPromedioDiaria * (tiempoEntrePedidos + tiempoDemoraProv) + valorNormalZ * desvEstandarTiempoPedidoYDemora);
            articulo.setCantidadMaximaArticulo(cantidadMaxima);
            articuloRepository.save(articulo);

            return cantidadMaxima;
        } catch (Exception e) {

            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Integer cantidadAPedir(Articulo articulo) throws Exception{
        try {
            Integer inventarioActual = articulo.getCantidadArticulo();
            Integer cantidadAPedir = articulo.getCantidadMaximaArticulo()- inventarioActual;

            return cantidadAPedir;

        } catch (Exception e) {

            throw new Exception(e.getMessage());
        }
    }


    public List<Articulo> listadoFaltantes() throws Exception{
        try{
            List<Articulo> todosArticulos = articuloRepository.findAll();
            List<Articulo> articulosFaltantes = new ArrayList<Articulo>();

            for(Articulo articulo : todosArticulos){
                Integer cantidadArticulo = articulo.getCantidadArticulo();
                Integer stockSeguridadArticulo = articulo.getStockSeguridadArticulo();

                if(cantidadArticulo!= null && stockSeguridadArticulo != null){
                    if(cantidadArticulo <= stockSeguridadArticulo){
                        articulosFaltantes.add(articulo);
                    }
                }
            }
            return articulosFaltantes;
        } catch (Exception e ){
            throw new Exception(e.getMessage());
        }
    }

    public List<Articulo> listadoAReponer() throws Exception{
        try{
            List<Articulo> todosArticulos = articuloRepository.findAll();
            List<Articulo> articulosAReponer = new ArrayList<Articulo>();

            for(Articulo articulo : todosArticulos){
                boolean ordenActiva = ordenCompraService.articuloConOrdenActiva(articulo.getId());
                if (articulo.getCantidadArticulo() != null && articulo.getPuntoPedidoArticulo() != null) {
                    if (articulo.getCantidadArticulo() <= articulo.getPuntoPedidoArticulo() && !ordenActiva) {

                        articulosAReponer.add(articulo);
                    }
                }
            }
            return articulosAReponer;
        } catch (Exception e ){
            throw new Exception(e.getMessage());
        }
    }

    //METODOS PARA CUANDO MODIFICA UN ARTICULO
    public void sacarIntervaloFijo(Articulo articulo) throws Exception{
        try {
            // si se quiere sacar el intervalo fijo del articulo
            articulo.setCantidadMaximaArticulo(null);
            articulo.setTiempoRevisionArticulo(null);
            articulo.setModeloInventario(ModeloInventario.MODELO_LOTE_FIJO);
            articuloRepository.save(articulo); //se guarda para que los metodos de lote fijo lean esto en null
        }catch (Exception e ) {
            throw new Exception(e.getMessage());
        }
    }
    public void sacarLoteFijo(Articulo articulo) throws Exception{
        try{
            // si se quiere sacar el lote fijo del articulo
            articulo.setModeloInventario(ModeloInventario.MODELO_INTERVALO_FIJO);
            articulo.setLoteOptimoArticulo(null);
            articulo.setPuntoPedidoArticulo(null);
            articuloRepository.save(articulo); //se guarda para que los metodos de intervalo fijo lean esto en null
        }catch (Exception e ) {
            throw new Exception(e.getMessage());
        }
    }

    public void modificarModeloInventarioArticulo(Articulo articulo) throws Exception{
        try{

            if(articulo.getModeloInventario().equals(ModeloInventario.MODELO_LOTE_FIJO)){

                sacarIntervaloFijo(articulo); //deja en null lo del intervalo fijo
                calculosModeloLoteFijo(articulo.getId()); //setea lote optimo, pp y ss, y lo guarda en bd
            }
            if(articulo.getModeloInventario().equals(ModeloInventario.MODELO_INTERVALO_FIJO)){
                sacarLoteFijo(articulo);
                calculosModeloIntervaloFijo(articulo.getId());
            }

        } catch (Exception e ) {
            throw new Exception(e.getMessage());
        }
    }
    public void modificarValoresSegunProveedor(Articulo articulo, Proveedor proveedor) throws Exception{
        try{
            ProveedorArticulo proveedorArticuloPred = proveedorArticuloService.findProveedorArticuloByAmbosIds(articulo.getId(), proveedor.getId());
            articulo.setCostoAlmacenamientoArticulo(proveedorArticuloPred.getCostoAlmacenamientoArticuloProveedor());
            articulo.setCostoPedidoArticulo(proveedorArticuloPred.getCostoPedidoArticuloProveedor());
            articuloRepository.save(articulo);
        }catch (Exception e ) {
            throw new Exception(e.getMessage());
        }
    }

    public Articulo modificarArticulo(ModificarArticuloDTO articuloAModificar) throws Exception{
        try{
            Articulo nuevoArticulo = new Articulo();
            nuevoArticulo.setNombreArticulo(articuloAModificar.getNombreArticulo());
            nuevoArticulo.setId(articuloAModificar.getIdArticulo());
            nuevoArticulo.setTiempoRevisionArticulo(articuloAModificar.getTiempoRevisionArticulo());

            Proveedor proveedorPredeterminado = proveedorService.findById(articuloAModificar.getProveedorPredeterminadoId());
            nuevoArticulo.setProveedorPredeterminado(proveedorPredeterminado);
            nuevoArticulo.setModeloInventario(articuloAModificar.getModeloInventario());

            Optional<Articulo> articuloOpcional = articuloRepository.findById(nuevoArticulo.getId());
            Articulo articuloExistente = articuloOpcional.orElseThrow(() -> new EntityNotFoundException("Entidad no encontrada con el id: " + nuevoArticulo.getId()));

            BeanUtils.copyProperties(nuevoArticulo, articuloExistente, getNullPropertyNames(nuevoArticulo));
            Articulo articuloModificado = articuloOpcional.get();
            articuloModificado.setProveedorPredeterminado(proveedorPredeterminado);
            articuloRepository.save(articuloModificado);

            modificarValoresSegunProveedor(articuloModificado, articuloModificado.getProveedorPredeterminado());
            modificarModeloInventarioArticulo(articuloModificado);

            return articuloModificado;

        }catch (Exception e ) {
            throw new Exception(e.getMessage());
        }
    }


    private String[] getNullPropertyNames(Articulo source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public Articulo crearArticulo(Articulo articuloCreado, ProveedorArticulo proveedorArticulo) throws Exception{
        try {
            articuloRepository.save(articuloCreado);

            proveedorArticulo.setArticulo(articuloCreado);
            proveedorArticuloService.save(proveedorArticulo);
            modificarValoresSegunProveedor(articuloCreado, articuloCreado.getProveedorPredeterminado());
            modificarModeloInventarioArticulo(articuloCreado);

            return articuloCreado;
        }catch (Exception e ) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Proveedor obtenerProveedorPredeterminado(Long articuloId) {
        Articulo articulo = articuloRepository.findById(articuloId)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));
        return articulo.getProveedorPredeterminado();
    }

}
