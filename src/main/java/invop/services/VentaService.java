package invop.services;

import invop.dto.VentaDto;
import invop.entities.Articulo;
import invop.entities.Venta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface VentaService extends BaseService<Venta, Long> {

    List<Venta> findVentasByFechas(LocalDate fechaDesde, LocalDate fechaHasta) throws Exception;
    Venta crearVenta(VentaDto ventaDto) throws Exception;

    }
