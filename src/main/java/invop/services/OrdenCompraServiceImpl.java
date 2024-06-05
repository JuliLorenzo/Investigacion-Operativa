package invop.services;

import invop.entities.OrdenCompra;
import invop.repositories.OrdenCompraRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

import java.util.List;

@Service
public class OrdenCompraServiceImpl extends BaseServiceImpl<OrdenCompra, Long> implements OrdenCompraService {

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;


    public OrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository, OrdenCompraDetalleService ordenCompraDetalleService){
        super(ordenCompraRepository);
        this.ordenCompraRepository = ordenCompraRepository;

    }


    @Override
    @Transactional
    public List<OrdenCompra> findOrdenCompraByEstado(String filtroEstado) throws Exception{
        try {
            List<OrdenCompra> buscarOrdenes = ordenCompraRepository.findOrdenCompraByEstado(filtroEstado);
            return buscarOrdenes;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }


    //SEGUIRLO
    @Override
    @Transactional
    public boolean controlEstadoOrdenes() throws Exception{
        try{
            return true;
        }catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }
}
