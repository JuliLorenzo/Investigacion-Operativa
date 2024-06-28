package invop.services;

import invop.entities.Articulo;
import invop.entities.OrdenCompra;
import invop.entities.OrdenCompraDetalle;
import invop.entities.ProveedorArticulo;
import invop.enums.EstadoOrdenCompra;
import org.aspectj.weaver.ast.Or;

import java.util.List;

public interface OrdenCompraService extends BaseService<OrdenCompra, Long> {
    public List<OrdenCompra> findOrdenCompraByEstado(EstadoOrdenCompra estadoOrdenCompra) throws Exception;
    public boolean articuloConOrdenActiva(Long articuloId) throws Exception;
    public OrdenCompra crearOrdenCompraAutomatica(Articulo articulo) throws Exception;
    public OrdenCompra modificarOCAutomatica(Long idOrdenCompra, Long idProveedor, Integer nuevaCantidad) throws Exception;
    public OrdenCompra confirmarOrdenCompra(Long ordenCompraId);
    public OrdenCompra cancelarOrdenCompra(Long ordenCompraId);
    public OrdenCompra finalizarOrdenCompra(Long ordenCompraId);

    }
