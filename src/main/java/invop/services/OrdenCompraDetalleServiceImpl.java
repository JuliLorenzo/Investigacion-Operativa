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
        //Busca los detalles las ordenes de compra del Articulo
        List<OrdenCompraDetalle> detalles = ordenCompraDetalleRepository.findByArticuloId(articuloId);

        //Controla si algun detalle corresponde a una Orden de Compra Activa
        for (OrdenCompraDetalle detalle : detalles) {
            OrdenCompra ordenCompra = detalle.getOrdenCompra();
            if (ordenCompra != null &&
                    ("En curso".equalsIgnoreCase(ordenCompra.getEstadoOrdenCompra()) ||
                            "Pendiente".equalsIgnoreCase(ordenCompra.getEstadoOrdenCompra()))) {
                return true;
            }
        }
        return false;
    }
    }







