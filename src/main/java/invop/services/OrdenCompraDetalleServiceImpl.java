package invop.services;

import invop.entities.*;
import invop.repositories.OrdenCompraDetalleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenCompraDetalleServiceImpl extends BaseServiceImpl<OrdenCompraDetalle, Long> implements OrdenCompraDetalleService {

    @Autowired
    private OrdenCompraDetalleRepository ordenCompraDetalleRepository;


    public OrdenCompraDetalleServiceImpl(OrdenCompraDetalleRepository ordenCompraDetalleRepository) {
        super(ordenCompraDetalleRepository);
        this.ordenCompraDetalleRepository = ordenCompraDetalleRepository;
    }


    public List<OrdenCompraDetalle> buscarDetallesPorOC(Long idOC) throws Exception{
        try{
            List<OrdenCompraDetalle> listaDetalles = ordenCompraDetalleRepository.findDetallesByOC(idOC);
            return listaDetalles;
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
}







