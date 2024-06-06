package invop.services;

import invop.entities.Articulo;

import java.util.List;

public interface ArticuloService extends BaseService<Articulo, Long> {

    //METODOS PARA EL EOQ
    public int calculoLoteOptimo(int demandaAnterior, double costoPedido, double costoAlmacenamiento) throws Exception;
    public int calculoPuntoPedido(int demandaAnterior, double tiempoDemoraProveedor) throws Exception;
    public int calculoStockSeguridad() throws Exception;

    public void metodoLoteFijo(Long idArticulo,int demandaAnual, double costoPedido, double costoAlmacenamiento, double tiempoDemoraProveedor) throws Exception;

    public boolean articuloConOrdenCompraActiva(Long idArticulo) throws Exception;
    public boolean controlOrdenCompraActiva(Long idArticulo) throws Exception;

    }
