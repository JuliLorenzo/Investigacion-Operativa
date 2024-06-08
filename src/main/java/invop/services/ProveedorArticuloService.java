package invop.services;

import invop.entities.OrdenCompra;
import invop.entities.ProveedorArticulo;

import java.util.List;

public interface ProveedorArticuloService extends BaseService<ProveedorArticulo, Long> {

    public List<ProveedorArticulo> findProveedoresByArticulo(String filtroArticulo) throws Exception;

    public List<ProveedorArticulo> findArticulosByProveedor(String filtroProveedor) throws Exception;
    public Double findCostoPedido(String nombreArticulo, String nombreProveedor) throws Exception;
    public Double obtenerTiempoDemoraPromedioProveedores(String filtroArticulo) throws Exception;

}
