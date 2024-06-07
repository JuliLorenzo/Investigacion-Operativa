package invop.services;

import invop.entities.OrdenCompra;
import invop.entities.ProveedorArticulo;

import java.util.List;

public interface ProveedorArticuloService extends BaseService<ProveedorArticulo, Long> {

    public List<Object> findProveedoresByArticulo(String filtroArticulo) throws Exception;

    public List<Object> findArticulosByProveedor(String filtroProveedor) throws Exception;

    public Double calculoCGI(Double costoAlmacenamiento, Double costoPedido, Double precioArticulo, Double cantidadAComprar, Double demandaAnual) throws Exception;

    public void guardarValorCGI(Double valorCGI, ProveedorArticulo proveedorArticulo) throws Exception;

}
