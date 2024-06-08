package invop.services;

import invop.entities.Articulo;
import invop.entities.ProveedorArticulo;
import invop.repositories.ArticuloRepository;
import invop.repositories.BaseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;


import java.util.List;
import java.util.Optional;

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


    public ArticuloServiceImpl(BaseRepository<Articulo, Long> baseRepository, ArticuloRepository articuloRepository, OrdenCompraService ordenCompraService, DemandaHistoricaService demandaHistoricaService, ProveedorArticuloService proveedorArticuloService) {
        super(baseRepository);
        this.articuloRepository = articuloRepository;
        this.ordenCompraService = ordenCompraService;
        this.demandaHistoricaService = demandaHistoricaService;
        this.proveedorArticuloService = proveedorArticuloService;
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


    public Double calculoCGI(Double costoAlmacenamiento, Double costoPedido, Double precioArticulo, Double cantidadAComprar, Double demandaAnual) throws Exception {
        try {
            Double costoCompra = precioArticulo * cantidadAComprar;
            Double cgi = costoCompra + costoAlmacenamiento * (cantidadAComprar/2) + costoPedido * (demandaAnual/cantidadAComprar);
            return cgi;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void guardarValorCGI(Double valorCGI, Articulo Articulo) throws Exception{
        Articulo.setCgiArticulo(valorCGI);
        articuloRepository.save(Articulo);

    }


    @Override
    @Transactional
    public int calculoLoteOptimo(int demandaAnual, double costoPedido, double costoAlmacenamiento) throws Exception {
        //ESTE ES DEL METODO DE TAMAÑO FIJO DE LOTE
        try{
            int loteOptimo = 0;
            loteOptimo = (int)Math.sqrt((2 * demandaAnual * costoPedido) / costoAlmacenamiento);
            return loteOptimo;
        }
        catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }


    // METODO LOTE FIJO: Calculo Lote Optimo -> Dividirlo
    @Override
    @Transactional
    public int calculoDeLoteOptimo(Long idArticulo) throws Exception {
        try{
            //Buscar el Articulo
            Articulo articulo = findArticuloById(idArticulo);

            //Obtener fecha actual y fecha de hace un año
            LocalDate fechaActual = LocalDate.now();
            LocalDate fechaHaceUnAno = fechaActual.minusYears(1);

            //Buscar ProveedorPredeterminado para obtener el CP
            String proveedorPredeterminado = articulo.getProveedorPredeterminado().getNombreProveedor();

            int demandaAnual = demandaHistoricaService.calcularDemandaHistorica(fechaHaceUnAno,fechaActual ,idArticulo);
            double costoAlmacenamiento = articulo.getCostoAlmacenamientoArticulo();
            double costoPedido = proveedorArticuloService.findCostoPedido(articulo.getNombreArticulo(), proveedorPredeterminado);

            int loteOptimo = (int)Math.sqrt((2 * demandaAnual * costoPedido) / costoAlmacenamiento);
            return loteOptimo;
        }
        catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }



    @Override
    @Transactional
    public int calculoPuntoPedido(int demandaAnual, double tiempoDemoraProveedor) throws Exception{
        //ESTE ES DEL METODO DE TAMAÑO FIJO DE LOTE
        try {
            int puntoPedido = demandaAnual * (int)Math.round(tiempoDemoraProveedor);
            return puntoPedido;
        } catch(Exception e ){
            throw new Exception(e.getMessage());
        }

    }

    @Override
    @Transactional
    public int calculoStockSeguridad() throws Exception{
        //ESTE ES DEL METODO DE TAMAÑO FIJO DE LOTE
        try {
            return 0; //LO DEJO EN CERO PQ TODAVIA NO SE COMO SE CALCULA xd
        }catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void metodoLoteFijo(Long idArticulo,int demandaAnterior, double costoPedido, double costoAlmacenamiento, double tiempoDemoraProveedor) throws Exception{
        try {
            int loteOptimoCalculado = calculoLoteOptimo(demandaAnterior, costoPedido, costoAlmacenamiento);
            int puntoPedidoCalculado = calculoPuntoPedido(demandaAnterior, tiempoDemoraProveedor);

            Articulo articulo = articuloRepository.findById(idArticulo).orElseThrow(() -> new Exception("Articulo no encontrado"));

            articulo.setLoteOptimoArticulo(loteOptimoCalculado);
            articulo.setPuntoPedidoArticulo(puntoPedidoCalculado);

        } catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }

    //METODOS PARA EL MODELO INTERVALO FIJO
    public int cantidadMeta() throws Exception{
        // Implementación futura
        return 0;
    }
    public int cantidadAPedir() throws Exception{
        // Implementación futura
        return 0;
    }
    public int metodoIntervaloFijo(Long idProveedorArticulo) throws Exception{
        // Implementación futura
        return 0;
    }

    //METODO PARA LA GENERACION DE LA ORDEN COMPRA

    //Buscar proveedor predeterminado


    //Buscar proveedores existentes para un Articulo



}
