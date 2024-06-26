package invop.repositories;

import invop.entities.Venta;
import invop.entities.VentaDetalle;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaDetalleRepository extends BaseRepository<VentaDetalle, Long> {

    @Query(
            value = "SELECT * " +
                    "FROM venta_detalles " +
                    "WHERE id_articulo = :filtroArticulo ",
            nativeQuery = true
    )
    List<VentaDetalle> findDetallesByArticulo(@PathVariable("filtroArticulo") Long filtroArticulo);

    @Query(
            value = "SELECT * " +
                    "FROM venta_detalles " +
                    "WHERE id_venta = :filtroVenta ",
            nativeQuery = true
    )
    List<VentaDetalle> findDetallesByVenta(@PathVariable("filtroVenta") Long filtroVenta);

}
