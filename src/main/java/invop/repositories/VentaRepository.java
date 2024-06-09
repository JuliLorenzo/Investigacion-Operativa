package invop.repositories;

import invop.entities.Venta;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface VentaRepository extends BaseRepository<Venta, Long> {
    @Query(
            value = "SELECT * " +
                    "FROM ventas " +
                    "WHERE fecha_venta " +
                    "BETWEEN :fechaDesde AND :fechaHasta",
            nativeQuery = true
    )
    List<Venta> findVentasByFechas(@Param("fechaDesde") LocalDate fechaDesde, @Param("fechaHasta") LocalDate fechaHasta);

    @Query(
            value = "SELECT AVG(demanda_diaria) " +
                    "FROM ( " +
                    "    SELECT v.fecha_venta, SUM(vd.cantidad_vendida) AS demanda_diaria " +
                    "    FROM ventas v " +
                    "       JOIN venta_detalles vd ON v.id = vd.id_venta " +
                    "    WHERE vd.id_articulo = :filtroArticulo " +
                    "    GROUP BY v.fecha_venta " +
                    ") AS subquery",
            nativeQuery = true
    )
    int calcularDemandaPromedioDiariaDeArticulo(@Param("filtroArticulo") Long filtroArticulo);
}
