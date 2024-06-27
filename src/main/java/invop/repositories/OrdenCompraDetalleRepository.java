package invop.repositories;

import invop.entities.OrdenCompra;
import invop.entities.OrdenCompraDetalle;
import invop.entities.Venta;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdenCompraDetalleRepository extends BaseRepository<OrdenCompraDetalle, Long> {
    @Query(
            value = "SELECT * " +
                    "FROM orden_compra_detalles " +
                    "WHERE id_articulo = :idArticulo ",
            nativeQuery = true
    )
    List<OrdenCompraDetalle> buscarDetallesPorArticulo(@Param("idArticulo") Long idArticulo);

}


