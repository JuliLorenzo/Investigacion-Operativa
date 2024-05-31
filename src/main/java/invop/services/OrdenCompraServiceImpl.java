package invop.services;

import invop.entities.OrdenCompra;
import invop.repositories.OrdenCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class OrdenCompraServiceImpl extends BaseServiceImpl<OrdenCompra, Long> implements OrdenCompraService {

    @Autowired
    public OrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository){
        super(ordenCompraRepository);
    }
}
