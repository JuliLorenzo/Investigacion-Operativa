package invop.repositories;

import invop.entities.OrdenCompra;
import invop.entities.OrdenCompraDetalle;
import invop.entities.Venta;
import invop.entities.VentaDetalle;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdenCompraDetalleRepository extends BaseRepository<OrdenCompraDetalle, Long> {
    List<OrdenCompraDetalle> findByArticuloId(Long articuloId);


    @Query(
            value = "SELECT * " +
                    "FROM orden_compra_detalles " +
                    "WHERE id_orden_compra = :filtroOC ",
            nativeQuery = true
    )
    List<OrdenCompraDetalle> findDetallesByOC(@PathVariable("filtroOC") Long filtroOC);

}


