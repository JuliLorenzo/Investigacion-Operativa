package invop.repositories;

import invop.entities.OrdenCompra;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenCompraRepository extends BaseRepository<OrdenCompra, Long> {
    @Query(
            value = "SELECT * FROM ordenes_compra WHERE estado_orden_compra LIKE %:filtroEstado%",
            nativeQuery = true
    )
    List<OrdenCompra> findOrdenCompraByEstado(@Param("filtroEstado") String filtroEstado);
}
