package invop.services;

import invop.entities.Venta;

import java.util.Date;
import java.util.List;

public interface VentaService extends BaseService<Venta, Long> {
    List<Venta> findVentasByFechas(Date fechaDesde, Date fechaHasta) throws Exception;

}
