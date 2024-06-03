package invop.services;

import invop.entities.Venta;
import invop.entities.VentaDetalle;
import invop.repositories.BaseRepository;
import invop.repositories.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class VentaServiceImpl extends BaseServiceImpl<Venta, Long> implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;
    public VentaServiceImpl(BaseRepository<Venta, Long> baseRepository, VentaRepository ventaRepository){
        super(baseRepository);
        this.ventaRepository = ventaRepository;
    }

    @Override
    public List<Venta> findVentasByFechas(LocalDate fechaDesde, LocalDate fechaHasta) throws Exception {
        try {
            List<Venta> buscarVentas = ventaRepository.findVentasByFechas(fechaDesde, fechaHasta);
            return buscarVentas;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public double calcularDemandaHistoricaArticulo(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo) {
        List<Venta> ventas = ventaRepository.findVentasByFechas(fechaDesde, fechaHasta);

        double cantidadTotalVendida = 0;

        //recorrer ventas y obtener la cantidad vendida del articulo
        for (Venta venta : ventas) {
            cantidadTotalVendida += calcularCantidadVendidaArticulo(venta, idArticulo);
        }

        return cantidadTotalVendida;
    }

    private double calcularCantidadVendidaArticulo(Venta venta, Long idArticulo) {
        double cantidadTotalVendida = 0;

        for (VentaDetalle detalle : venta.getVentaDetalles()) {
            if (detalle.getArticulo().getId().equals(idArticulo)) {
                cantidadTotalVendida += detalle.getCantidadVendida();
            }
        }
        return cantidadTotalVendida;
    }
}


