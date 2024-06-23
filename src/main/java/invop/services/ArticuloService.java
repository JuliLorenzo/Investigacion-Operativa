package invop.services;

import invop.entities.Articulo;
import invop.entities.Proveedor;
import invop.entities.ProveedorArticulo;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

public interface ArticuloService extends BaseService<Articulo, Long> {
    public Articulo findArticuloById(Long id);
    public boolean controlOrdenCompraActiva(Long idArticulo) throws Exception;

    public void darDeBajaArticulo(Long idArticulo) throws Exception;
    public List<Long> getArticulosSinStock(Map<String, Integer> articulosDetalleVenta);
    public void disminuirStock(Articulo articulo, Integer cantVendida) throws Exception;
    public Double calculoCGI(Long idArticulo) throws Exception;
    public void guardarValorCGI(Double valorCGI, Articulo Articulo) throws Exception;

    //METODOS PARA EL EOQ
    public void calculosModeloLoteFijo(Long idArticulo) throws Exception;
    public void calculosModeloIntervaloFijo(Long idArticulo) throws Exception;

    public void guardarPuntoPedido(Integer valorPP, Articulo Articulo) throws Exception;
    public int calculoStockSeguridad(Long idArticulo) throws Exception;
    public void guardarStockSeguridad(Integer valorSS, Articulo Articulo) throws Exception;

    public int calculoPuntoPedido(Long idArticulo) throws Exception;
    public Integer calculoDemandaAnual(Long idArticulo) throws Exception;
    public int calculoDeLoteOptimo(Long idArticulo) throws Exception;

    //METODOS PARA EL MODELO INTERVALO FIJO

    //METODOS PARA EL MODELO INTERVALO FIJO
    Integer cantidadMaxima(Articulo articulo) throws Exception;
    Integer cantidadAPedir(Articulo articulo) throws Exception;

    public List<Articulo> listadoFaltantes() throws Exception;
    public List<Articulo> listadoAReponer() throws Exception;

    //para cuando modifica un articulo
    public void modificarValoresSegunProveedor(Articulo articulo, Proveedor proveedor) throws Exception;
    public void modificarModeloInventarioArticulo(Articulo articulo) throws Exception;
    public void sacarIntervaloFijo(Articulo articulo) throws Exception;
    public void sacarLoteFijo(Articulo articulo) throws Exception;
    public Articulo modificarArticulo(Long idArticulo,  Articulo nuevoArticulo) throws Exception;
    //fin

    public Articulo crearArticulo(Articulo articuloCreado, ProveedorArticulo proveedorArticulo) throws Exception;

    Proveedor obtenerProveedorPredeterminado(Long articuloId);

}
