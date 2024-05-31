package invop.services;

import invop.entities.Venta;
import invop.repositories.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class VentaServiceImpl extends BaseServiceImpl<Venta, Long> implements VentaService {

    @Autowired
    public VentaServiceImpl(VentaRepository ventaRepository){
        super(ventaRepository);
    }
}
