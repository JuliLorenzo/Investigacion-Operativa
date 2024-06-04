package invop.services;

import invop.entities.Articulo;

import java.util.List;

public interface ArticuloService extends BaseService<Articulo, Long> {

    //METODOS PARA EL EOQ
    public int calculoLoteOptimo(int demandaAnterior, double costoPedido, double costoAlmacenamiento) throws Exception;
    public int calculoPuntoPedido(int demandaAnterior, double tiempoDemoraProveedor) throws Exception;
    public int calculoStockSeguridad() throws Exception;

    public void metodoLoteFijo(Long idArticulo,int demandaAnterior, double costoPedido, double costoAlmacenamiento, double tiempoDemoraProveedor) throws Exception;
    //
}
