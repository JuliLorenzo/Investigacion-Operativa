package invop.services;

import invop.entities.VentaDetalle;
import invop.repositories.VentaDetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class VentaDetalleServiceImpl extends BaseServiceImpl<VentaDetalle, Long> implements VentaDetalleService {

    @Autowired
    public VentaDetalleServiceImpl(VentaDetalleRepository ventaDetalleRepository){
        super(ventaDetalleRepository);
    }
}
