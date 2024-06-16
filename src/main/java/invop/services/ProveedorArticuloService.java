package invop.services;

import invop.entities.ProveedorArticulo;

import java.util.List;

public interface ProveedorArticuloService extends BaseService<ProveedorArticulo, Long> {

    public List<ProveedorArticulo> findProveedoresByArticulo(Long filtroArticulo) throws Exception;

    public ProveedorArticulo findProveedorArticuloByAmbosIds(Long filtroArticulo, Long filtroProveedor) throws Exception;
    public List<ProveedorArticulo> findArticulosByProveedor(Long filtroProveedor) throws Exception;

    //PROBAR Y ELEGIR UN CALCULO DE CP
    public Double findCostoPedido(Long idArticulo, Long idProveedor) throws Exception;
    public Double findCostoPedidoByArticuloAndProveedor(Long idArticulo, Long idProveedor) throws Exception;
    public Double findTiempoDemoraArticuloByArticuloAndProveedor(Long idArticulo, Long idProveedor) throws Exception;


    //
    // COMENTO ESTOS MÉTODOS PARA QUE NO TIRE ERROR, PORQUE AÚN NO LOS IMPLEMENTAMOS
    //

    // public Double calculoCGI(Double costoAlmacenamiento, Double costoPedido, Double precioArticulo, Double cantidadAComprar, Double demandaAnual) throws Exception;
    // public void guardarValorCGI(Double valorCGI, ProveedorArticulo proveedorArticulo) throws Exception;

    //METODOS PARA EL EOQ
    // public int calculoLoteOptimo(int demandaAnterior, double costoPedido, double costoAlmacenamiento) throws Exception;
    // public int calculoPuntoPedido(int demandaAnterior, double tiempoDemoraProveedor) throws Exception;
    // public int calculoStockSeguridad() throws Exception;
    // public void metodoLoteFijo(Long idArticulo,int demandaAnual, double costoPedido, double costoAlmacenamiento, double tiempoDemoraProveedor) throws Exception;

    //METODOS PARA EL MODELO INTERVALO FIJO
    // public int metodoIntervaloFijo(Long idProveedorArticulo) throws Exception;

}
