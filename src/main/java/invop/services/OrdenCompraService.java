package invop.services;

import invop.entities.Articulo;
import invop.entities.OrdenCompra;
import invop.entities.OrdenCompraDetalle;
import invop.entities.ProveedorArticulo;
import org.aspectj.weaver.ast.Or;

import java.util.List;

public interface OrdenCompraService extends BaseService<OrdenCompra, Long> {
    // Métodos específicos de Orden de Compra
    public List<OrdenCompra> findOrdenCompraByEstado(String filtroEstado) throws Exception;
    public boolean articuloConOrdenActiva(Long articuloId) throws Exception;
    public OrdenCompra crearOrdenCompraAutomatica(Articulo articulo) throws Exception;
    public OrdenCompra confirmarOrdenCompra(Long ordenCompraId);
    public OrdenCompra cancelarOrdenCompra(Long ordenCompraId);
    public OrdenCompra finalizarOrdenCompra(Long ordenCompraId);

    }
