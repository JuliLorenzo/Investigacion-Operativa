package invop.services;

import invop.entities.OrdenCompra;
import invop.repositories.OrdenCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdenCompraServiceImpl extends BaseServiceImpl<OrdenCompra, Long> implements OrdenCompraService {

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;
    public OrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository){
        super(ordenCompraRepository);
        this.ordenCompraRepository = ordenCompraRepository;
    }
}
