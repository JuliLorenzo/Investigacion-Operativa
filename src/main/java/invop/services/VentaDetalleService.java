package invop.services;

import invop.entities.Base;
import invop.entities.VentaDetalle;

import java.util.List;

public interface VentaDetalleService extends BaseService<VentaDetalle, Long> {

    public List<VentaDetalle> buscarDetallesPorIdArticulo(Long idArticulo) throws Exception;

    public List<VentaDetalle> buscarDetallesPorVenta(Long idVenta) throws Exception;

}
