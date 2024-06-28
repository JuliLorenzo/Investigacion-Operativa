package invop.services;

import invop.entities.ProveedorArticulo;

import java.util.List;

public interface ProveedorArticuloService extends BaseService<ProveedorArticulo, Long> {

    public List<ProveedorArticulo> findProveedoresByArticulo(Long filtroArticulo) throws Exception;

    public ProveedorArticulo findProveedorArticuloByAmbosIds(Long filtroArticulo, Long filtroProveedor) throws Exception;
    public List<ProveedorArticulo> findArticulosByProveedor(Long filtroProveedor) throws Exception;
    public Double findCostoPedido(Long idArticulo, Long idProveedor) throws Exception;
    public Double findCostoPedidoByArticuloAndProveedor(Long idArticulo, Long idProveedor) throws Exception;
    public Double findTiempoDemoraArticuloByArticuloAndProveedor(Long idArticulo, Long idProveedor) throws Exception;
    public Double findPrecioArticuloByArticuloAndProveedor(Long idArticulo, Long idProveedor) throws Exception;


}
