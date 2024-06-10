package invop.services;

import invop.dto.VentaDto;
import invop.entities.Articulo;
import invop.entities.Venta;
import invop.entities.VentaDetalle;
import invop.repositories.ArticuloRepository;
import invop.repositories.BaseRepository;
import invop.repositories.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class VentaServiceImpl extends BaseServiceImpl<Venta, Long> implements VentaService {
    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private ArticuloService articuloService;

    public VentaServiceImpl(BaseRepository<Venta, Long> baseRepository, VentaRepository ventaRepository, ArticuloService articuloService){
        super(baseRepository);
        this.ventaRepository = ventaRepository;
        this.articuloService = articuloService;
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

    public Integer calcularDemandaHistoricaArticulo(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo) {
        List<Venta> ventas = ventaRepository.findVentasByFechas(fechaDesde, fechaHasta);

        int cantidadTotalVendida = 0;

        //recorrer ventas y acumular la cantidad vendida del articulo
        for (Venta venta : ventas) {
            for (VentaDetalle detalle : venta.getVentaDetalles()) {
                if (detalle.getArticulo().getId().equals(idArticulo)) {
                    cantidadTotalVendida = cantidadTotalVendida + detalle.getCantidadVendida();

                }
            }
        }
        return cantidadTotalVendida;
    }

    //Crear una Venta
    public Venta crearVenta(VentaDto ventaDto) throws Exception {
        Venta nuevaVenta = new Venta();
        for (Map.Entry<Long,Integer> item : ventaDto.getArticulosDetalleVenta().entrySet()) {
            Long idArticulo = item.getKey();
            Integer cantidad = item.getValue();

            Articulo articulo = articuloService.findById(idArticulo);
            nuevaVenta.agregarDetalleVenta(new VentaDetalle(articulo, cantidad));
        }
        nuevaVenta.setFechaVenta(ventaDto.getFechaHora());
        ventaRepository.save(nuevaVenta);
        return nuevaVenta;
    }

}


