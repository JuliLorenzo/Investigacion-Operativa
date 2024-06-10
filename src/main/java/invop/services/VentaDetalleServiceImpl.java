package invop.services;

import invop.entities.Articulo;
import invop.entities.VentaDetalle;
import invop.repositories.VentaDetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VentaDetalleServiceImpl extends BaseServiceImpl<VentaDetalle, Long> implements VentaDetalleService {

    @Autowired
    private VentaDetalleRepository ventaDetalleRepository;
    public VentaDetalleServiceImpl(VentaDetalleRepository ventaDetalleRepository){
        super(ventaDetalleRepository);
        this.ventaDetalleRepository = ventaDetalleRepository;
    }

}
