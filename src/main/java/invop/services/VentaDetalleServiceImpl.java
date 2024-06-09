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

    /*
    //FALTA TERMINAR
    public List<VentaDetalle> crearListaDeDetalles() throws Exception{
        List<VentaDetalle> detallesDeLaVenta = new ArrayList<>();
        detallesDeLaVenta.add(crearDetalle());
        return detallesDeLaVenta;
    }

    public VentaDetalle crearDetalle(Articulo articulo, Integer cantVendida) throws Exception {
        try {
            List<VentaDetalle> detallesDeVenta = new ArrayList<>();
            if (articulo.getCantidadArticulo()- cantVendida > 0 ){
                VentaDetalle nuevoDetalle = new VentaDetalle(articulo, cantVendida);
                detallesDeVenta.add(nuevoDetalle);
                //articulo.actualizarStock(Integer cantVendida)
            }
        }

    }

     */
}
