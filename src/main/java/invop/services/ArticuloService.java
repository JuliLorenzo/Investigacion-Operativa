package invop.services;

import invop.entities.Articulo;
import invop.entities.ProveedorArticulo;

import java.util.List;

public interface ArticuloService extends BaseService<Articulo, Long> {
    public Articulo findArticuloById(Long id);
    public boolean controlOrdenCompraActiva(Long idArticulo) throws Exception;

    public void darDeBajaArticulo(Long idArticulo) throws Exception;
    public Double calculoCGI(Double costoAlmacenamiento, Double costoPedido, Double precioArticulo, Double cantidadAComprar, Double demandaAnual) throws Exception;

    public void guardarValorCGI(Double valorCGI, Articulo Articulo) throws Exception;

    //METODOS PARA EL EOQ
    public int calculoLoteOptimo(int demandaAnual, double costoPedido, double costoAlmacenamiento) throws Exception;
    public int calculoPuntoPedido(Long idArticulo) throws Exception;
    public void guardarPuntoPedido(Integer valorPP, Articulo Articulo) throws Exception;
    public int calculoStockSeguridad(Long idArticulo) throws Exception;
    public void guardarStockSeguridad(Integer valorSS, Articulo Articulo) throws Exception;

    public void metodoLoteFijo(Long idArticulo) throws Exception;
    public int calculoDemandaAnual(Long idArticulo) throws Exception;
    public int calculoDeLoteOptimo(Long idArticulo) throws Exception;

    //METODOS PARA EL MODELO INTERVALO FIJO
    public int metodoIntervaloFijo(Long idArticulo) throws Exception;


}
