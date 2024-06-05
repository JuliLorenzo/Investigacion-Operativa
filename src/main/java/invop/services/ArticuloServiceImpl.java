package invop.services;

import invop.entities.Articulo;
import invop.entities.OrdenCompra;
import invop.entities.OrdenCompraDetalle;
import invop.repositories.ArticuloRepository;
import invop.repositories.BaseRepository;
import invop.repositories.OrdenCompraRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ArticuloServiceImpl extends BaseServiceImpl<Articulo, Long> implements ArticuloService {

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private OrdenCompraService ordenCompraService;

    @Autowired
    private OrdenCompraDetalleService ordenCompraDetalleService;

    public ArticuloServiceImpl(BaseRepository<Articulo, Long> baseRepository, ArticuloRepository articuloRepository, OrdenCompraService ordenCompraService, OrdenCompraDetalleService ordenCompraDetalleService) {
        super(baseRepository);
        this.articuloRepository = articuloRepository;
        this.ordenCompraService = ordenCompraService;
        this.ordenCompraDetalleService = ordenCompraDetalleService;
    }

    @Override
    @Transactional
    public int calculoLoteOptimo(int demandaAnterior, double costoPedido, double costoAlmacenamiento) throws Exception {
        //ESTE ES DEL METODO DE TAMAÑO FIJO DE LOTE
        try{
            int loteOptimo = 0;
            loteOptimo = (int)Math.sqrt((2 * demandaAnterior * costoPedido) / costoAlmacenamiento);
            return loteOptimo;
        }
        catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public int calculoPuntoPedido(int demandaAnterior, double tiempoDemoraProveedor) throws Exception{
       //ESTE ES DEL METODO DE TAMAÑO FIJO DE LOTE
        try {
            int puntoPedido = demandaAnterior * (int)Math.round(tiempoDemoraProveedor);
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


    //!!!!!!!!! ESTO LO SEPARARIA, Q SEA UN METODO PARA BUSCAR LAS PENDIENTES Y OTRO PARA LAS Q ESTAN EN CURSO
    @Override
    @Transactional
    public boolean articuloConOrdenCompraActiva(Long idArticulo) throws Exception{
        List<OrdenCompra> ordenesPendientes = ordenCompraService.findOrdenCompraByEstado("Pendiente");
        List<OrdenCompra> ordenesEnCurso = ordenCompraService.findOrdenCompraByEstado("En curso");
        boolean existe = false;
        for(OrdenCompra ordenCompra : ordenesPendientes){
            for(OrdenCompraDetalle detalle : ordenCompra.getOrdenCompraDetalles()){
                if (detalle.getArticulo().getId().equals(idArticulo)){
                    existe = true;
                }
            }
        }
        for(OrdenCompra ordenCompra : ordenesEnCurso){
            for(OrdenCompraDetalle detalle : ordenCompra.getOrdenCompraDetalles()){
                if (detalle.getArticulo().getId().equals(idArticulo)){
                    existe = true;
                }
            }
        }
        return existe;
    }

    //Controla que el Articulo no tenga Ordenes de Compras Activas.
    @Transactional
    public boolean controlOrdenCompraActiva(Long idArticulo) throws Exception{
        try {
            boolean ordenActiva = ordenCompraDetalleService.articuloConOrdenActiva(idArticulo);
            return ordenActiva;

        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
}
