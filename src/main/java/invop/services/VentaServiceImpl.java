package invop.services;

import invop.dto.VentaDto;
import invop.entities.Articulo;
import invop.entities.Venta;
import invop.entities.VentaDetalle;
import invop.repositories.ArticuloRepository;
import invop.repositories.BaseRepository;
import invop.repositories.VentaDetalleRepository;
import invop.repositories.VentaRepository;
import jakarta.transaction.Transactional;
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
    @Autowired
    private VentaDetalleRepository ventaDetalleRepository;

    public VentaServiceImpl(BaseRepository<Venta, Long> baseRepository, VentaRepository ventaRepository, ArticuloService articuloService, VentaDetalleRepository ventaDetalleRepository){
        super(baseRepository);
        this.ventaRepository = ventaRepository;
        this.articuloService = articuloService;
        this.ventaDetalleRepository = ventaDetalleRepository;
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


    //Crear una Venta
    public Venta crearVenta(VentaDto ventaDto) throws Exception {
        Venta nuevaVenta = new Venta();
        for (Map.Entry<String,Integer> item : ventaDto.getArticulosDetalleVenta().entrySet()) {
            System.out.println("Entro a " + item.getKey() + ": " + item.getValue());
            String idArticuloStr = item.getKey();
            Long idArticulo = Long.parseLong(idArticuloStr);
            Integer cantidad = item.getValue();

            Articulo articulo = articuloService.findById(idArticulo);
            VentaDetalle nuevoDetalle = new VentaDetalle(articulo, cantidad);
            nuevaVenta.agregarDetalleVenta(nuevoDetalle);
            ventaDetalleRepository.save(nuevoDetalle);

            articuloService.disminuirStock(articulo, cantidad);
        }
        nuevaVenta.setFechaVenta(ventaDto.getFechaHora());

        ventaRepository.save(nuevaVenta);
        System.out.println("guardo venta");
        return nuevaVenta;
    }

}


