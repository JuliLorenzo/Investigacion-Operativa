package invop.services;

import invop.entities.OrdenCompra;
import invop.entities.ProveedorArticulo;

import java.util.List;

public interface ProveedorArticuloService extends BaseService<ProveedorArticulo, Long> {

    public List<ProveedorArticulo> findProveedoresByArticulo(Long filtroArticulo) throws Exception;

    public List<ProveedorArticulo> findArticulosByProveedor(Long filtroProveedor) throws Exception;
    public Double findCostoPedido(Long idArticulo, Long idProveedor) throws Exception;
    public Double obtenerTiempoDemoraPromedioProveedores(Long filtroArticulo) throws Exception;

}
