package invop.services;

import invop.entities.OrdenCompra;
import invop.entities.OrdenCompraDetalle;
import invop.entities.Venta;
import invop.repositories.OrdenCompraDetalleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenCompraDetalleServiceImpl extends BaseServiceImpl<OrdenCompraDetalle, Long> implements OrdenCompraDetalleService {

    @Autowired
    private OrdenCompraDetalleRepository ordenCompraDetalleRepository;
    public OrdenCompraDetalleServiceImpl(OrdenCompraDetalleRepository ordenCompraDetalleRepository){
        super(ordenCompraDetalleRepository);
        this.ordenCompraDetalleRepository = ordenCompraDetalleRepository;
    }

    public boolean articuloConOrdenActiva(Long articuloId) {
        List<OrdenCompraDetalle> detallesArticulo = ordenCompraDetalleRepository.findByArticuloId(articuloId);

        for (OrdenCompraDetalle detalle : detallesArticulo) {
            String estadoOrdenCompra = detalle.getOrdenCompra().getEstadoOrdenCompra();
            if (estadoOrdenCompra.equalsIgnoreCase("En curso") || estadoOrdenCompra.equalsIgnoreCase("Pendiente")) {
                return true;
            }
        }
        return false;
    }






}
