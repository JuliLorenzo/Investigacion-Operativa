package invop.services;

import invop.entities.OrdenCompraDetalle;
import invop.repositories.OrdenCompraDetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class OrdenCompraDetalleServiceImpl extends BaseServiceImpl<OrdenCompraDetalle, Long> implements OrdenCompraDetalleService {

    @Autowired
    public OrdenCompraDetalleServiceImpl(OrdenCompraDetalleRepository ordenCompraDetalleRepository){
        super(ordenCompraDetalleRepository);
    }
}
