package invop.services;

import invop.entities.Venta;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface VentaService extends BaseService<Venta, Long> {
    List<Venta> findVentasByFechas(LocalDate fechaDesde, LocalDate fechaHasta) throws Exception;

    Integer calcularDemandaHistoricaArticulo(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo);


    }
