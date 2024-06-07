package invop.services;

import invop.entities.OrdenCompra;
import invop.entities.ProveedorArticulo;

import java.util.List;

public interface ProveedorArticuloService extends BaseService<ProveedorArticulo, Long> {

    public List<Object> findProveedoresByArticulo(String filtroArticulo) throws Exception;

    public List<Object> findArticulosByProveedor(String filtroProveedor) throws Exception;

}
