package invop.services;

import invop.entities.*;
import invop.enums.ModeloInventario;
import invop.repositories.ArticuloRepository;
import invop.repositories.BaseRepository;
import invop.repositories.VentaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private VentaRepository ventaRepository;

    public ArticuloServiceImpl(BaseRepository<Articulo, Long> baseRepository, ArticuloRepository articuloRepository, OrdenCompraService ordenCompraService, DemandaHistoricaService demandaHistoricaService, ProveedorArticuloService proveedorArticuloService, ProveedorService proveedorService) {
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

    //Borrar articulo si no hay orden de compra activa
    public void darDeBajaArticulo(Long idArticulo) throws Exception{
        boolean ordenActiva = controlOrdenCompraActiva(idArticulo);
        try{
            if(!ordenActiva){
                Articulo articuloABorrar = articuloRepository.findById(idArticulo).orElseThrow(() -> new EntityNotFoundException("Articulo no encontrado"));
                articuloRepository.delete(articuloABorrar);
            }
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public List<Long> getArticulosSinStock(Map<String, Integer> articulosDetalleVenta){
        List<Long> articulosSinStock = new ArrayList<>();

        for (Map.Entry<String,Integer> item : articulosDetalleVenta.entrySet()) {
            String idArticuloStr = item.getKey();
            Long   idArticulo = Long.parseLong(idArticuloStr);
            Integer cantidad = item.getValue();

            if (articuloRepository.getById(idArticulo).getCantidadArticulo() < cantidad) {
                articulosSinStock.add(idArticulo);
            }
        }
        return articulosSinStock;
    }
    public void disminuirStock(Articulo articulo, Integer cantVendida){
        Integer nuevoStock = articulo.getCantidadArticulo() - cantVendida;
        articulo.setCantidadArticulo(nuevoStock);
        articuloRepository.save(articulo);
    }

    @Override
    @Transactional
    public Double calculoCGI(Long idArticulo) throws Exception {
        try {
            Articulo articulo = articuloRepository.findById(idArticulo).orElseThrow(() -> new Exception("Articulo no encontrado"));            Double precioArticulo = proveedorArticuloService.findPrecioArticuloByArticuloAndProveedor(idArticulo, articulo.getProveedorPredeterminado().getId());
            Integer cantidadAComprar = 0;

            if (articulo.getModeloInventario() == ModeloInventario.MODELO_LOTE_FIJO){
                cantidadAComprar = articulo.getLoteOptimoArticulo();
            }else {
                cantidadAComprar = cantidadAPedir(articulo);
            }

            Double costoCompra = precioArticulo * cantidadAComprar;
            Double CGI = costoCompra + articulo.getCostoAlmacenamientoArticulo() * (cantidadAComprar/2) + articulo.getCostoPedidoArticulo() * (articulo.getDemandaAnualArticulo()/cantidadAComprar);
            guardarValorCGI(CGI, articulo);

            return CGI;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void guardarValorCGI(Double valorCGI, Articulo Articulo) throws Exception{
        Articulo.setCgiArticulo(valorCGI);
        articuloRepository.save(Articulo);

    }

    // METODO LOTE FIJO:
    @Override
    @Transactional
    public void calculosModeloLoteFijo(Long idArticulo) throws Exception{
        try {
            Articulo articulo = articuloRepository.findById(idArticulo).orElseThrow(() -> new Exception("Articulo no encontrado"));
            Integer loteOptimo = calculoDeLoteOptimo(idArticulo);
            Integer puntoPedido = calculoPuntoPedido(idArticulo);
            Integer stockSeguridad = calculoStockSeguridad(idArticulo);
            puntoPedido += stockSeguridad;

            articulo.setLoteOptimoArticulo(loteOptimo);
            articulo.setPuntoPedidoArticulo(puntoPedido);
            articulo.setStockSeguridadArticulo(stockSeguridad);
            articuloRepository.save(articulo);
        }catch (Exception e ){
            throw new Exception(e.getMessage());
        }


    }
    @Override
    @Transactional
    public void calculosModeloIntervaloFijo(Long idArticulo) throws Exception {
        try {
            Articulo articulo = articuloRepository.findById(idArticulo).orElseThrow(() -> new Exception("Articulo no encontrado"));

            Integer stockSeguridad = calculoStockSeguridad(idArticulo);
            Integer cantidadMaxima = cantidadMaxima(articulo);

            articulo.setStockSeguridadArticulo(stockSeguridad);
            articulo.setCantidadMaximaArticulo(cantidadMaxima);
            articuloRepository.save(articulo);
        } catch (Exception e) {
            throw new Exception(e.getMessage());

        }
    }

    //Ver si mover el metodo a DemandaHistorica
    @Override
    @Transactional
    public Integer calculoDemandaAnual(Long idArticulo) throws Exception {
        try {
            // Obtener fecha actual y fecha de hace un año
            LocalDate fechaActual = LocalDate.now();
            LocalDate fechaHaceUnAno = fechaActual.minusYears(1);
            Integer demandaAnual = demandaHistoricaService.calcularDemandaHistorica(fechaHaceUnAno,fechaActual,idArticulo);
            // si el artículo recién se crea, se setea la demandaAnual = 30
            Articulo articulo = articuloRepository.findById(idArticulo).orElseThrow(() -> new Exception("Articulo no encontrado"));

            // mirar método calcularDemandaHistoricaArticulo de DemandaHistoricaServiceImpl
            // esto es para que si el artículo es nuevo, usa el atributo cargado en articulo como la demanda anual
            if (demandaAnual == -1) {
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
    @Transactional
    public int calculoDeLoteOptimo(Long idArticulo) throws Exception {
        try{
            //Buscar el Articulo
            Articulo articulo = findArticuloById(idArticulo);

            //Buscar ProveedorPredeterminado para obtener el CP
            Long proveedorPredeterminado = articulo.getProveedorPredeterminado().getId();

            int demandaAnual = calculoDemandaAnual(idArticulo);
            double costoAlmacenamiento = articulo.getCostoAlmacenamientoArticulo();
            double costoPedido = proveedorArticuloService.findCostoPedido(idArticulo, proveedorPredeterminado);

            int loteOptimo = (int)Math.sqrt((2 * demandaAnual * costoPedido) / costoAlmacenamiento);
            articulo.setLoteOptimoArticulo(loteOptimo);
            articuloRepository.save(articulo);
            return loteOptimo;
        }
        catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }


    @Transactional
    public int calculoPuntoPedido(Long idArticulo) throws Exception{
        try {
            Articulo articulo = findArticuloById(idArticulo);
            int demandaAnual = calculoDemandaAnual(idArticulo);
            Double tiempoProveedor = proveedorArticuloService.findTiempoDemoraArticuloByArticuloAndProveedor(articulo.getId(), articulo.getProveedorPredeterminado().getId());

            int puntoPedido = demandaAnual * tiempoProveedor.intValue();
            articulo.setPuntoPedidoArticulo(puntoPedido);
            articuloRepository.save(articulo);

            return puntoPedido;
        } catch(Exception e ){
            throw new Exception(e.getMessage());
        }

    }

    @Override
    @Transactional
    public int calculoStockSeguridad(Long idArticulo) throws Exception{
        try {
            // Para unificar, usamos este método para SS de Lote Fijo y de Intervalo Fijo
            Articulo articulo = findArticuloById(idArticulo);
            Double valorNormalZ = 1.64;
            Double tiempoProveedor = proveedorArticuloService.findTiempoDemoraArticuloByArticuloAndProveedor(articulo.getId(), articulo.getProveedorPredeterminado().getId());
            Integer tiempoRevision = articulo.getTiempoRevisionArticulo();

            // Si el artículo usa modelo de Lote Fijo, el tiempo de revisión o entre pedidos es null, por lo que lo tomamos como 0
            if (tiempoRevision == null) {
                tiempoRevision = 0;
            }
            int stockSeguridad = (int) (valorNormalZ * Math.sqrt(tiempoRevision + tiempoProveedor));
            articulo.setStockSeguridadArticulo(stockSeguridad);
            articuloRepository.save(articulo);

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
    @Transactional
    public Integer cantidadMaxima(Articulo articulo) throws Exception {
        try {
            Long idArticulo = articulo.getId();

            // Acá suponemos que vendemos los 365 días del año
            Integer demandaPromedioDiaria = calculoDemandaAnual(idArticulo) / 365;
            Integer tiempoEntrePedidos = articulo.getTiempoRevisionArticulo();
            Double tiempoDemoraProv = proveedorArticuloService.findTiempoDemoraArticuloByArticuloAndProveedor(articulo.getId(), articulo.getProveedorPredeterminado().getId());
            Double valorNormalZ = 1.64;
            Integer desvEstandarDemandaDiaria = 1;

            Double desvEstandarTiempoPedidoYDemora = Math.sqrt(tiempoEntrePedidos + tiempoDemoraProv) * desvEstandarDemandaDiaria;

            Integer cantidadMaxima = (int) (demandaPromedioDiaria * (tiempoEntrePedidos + tiempoDemoraProv) + valorNormalZ * desvEstandarTiempoPedidoYDemora);
            articulo.setCantidadMaximaArticulo(cantidadMaxima);
            articuloRepository.save(articulo);

            return cantidadMaxima;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Integer cantidadAPedir(Articulo articulo) throws Exception{
        try {
            Integer inventarioActual = articulo.getCantidadArticulo();
            Integer cantidadAPedir = (articulo.getCantidadMaximaArticulo()- inventarioActual);
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

    //para cuando modifica un articulo

    public void sacarIntervaloFijo(Articulo articulo) throws Exception{
        try {
            articulo.setCantidadMaximaArticulo(null);
            articulo.setTiempoRevisionArticulo(null);
            articulo.setModeloInventario(ModeloInventario.MODELO_LOTE_FIJO);
            articuloRepository.save(articulo);
        }catch (Exception e ) {
            throw new Exception(e.getMessage());
        }
    }
    public void sacarLoteFijo(Articulo articulo) throws Exception{
        try{
            articulo.setModeloInventario(ModeloInventario.MODELO_INTERVALO_FIJO);
            articulo.setLoteOptimoArticulo(null);
            articulo.setPuntoPedidoArticulo(null);
            articuloRepository.save(articulo);

        }catch (Exception e ) {
            throw new Exception(e.getMessage());
        }
    }
    public void modificarArticulo(Long idArticulo) throws Exception{
        try{
            Articulo articulo = articuloRepository.findById(idArticulo).orElseThrow(() -> new Exception("Articulo no encontrado"));
            if(articulo.getModeloInventario().equals(ModeloInventario.MODELO_LOTE_FIJO)){
                calculosModeloLoteFijo(idArticulo); //setea lote optimo, pp y ss
                sacarIntervaloFijo(articulo);
            }
            if(articulo.getModeloInventario().equals(ModeloInventario.MODELO_INTERVALO_FIJO)){
                calculosModeloIntervaloFijo(idArticulo); //CUIDADO!!!!! VER LO Q HACE ESTE METODO Q INVOCAMOS
                sacarLoteFijo(articulo);
            }

        } catch (Exception e ) {
            throw new Exception(e.getMessage());
        }
    }



}
