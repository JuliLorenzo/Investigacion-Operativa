package invop.services;

import invop.entities.OrdenCompra;

import java.util.List;

public interface OrdenCompraService extends BaseService<OrdenCompra, Long> {
    // Métodos específicos de Orden de Compra
    public List<OrdenCompra> findOrdenCompraByEstado(String filtroEstado) throws Exception;
    public boolean controlEstadoOrdenes() throws Exception;
}
