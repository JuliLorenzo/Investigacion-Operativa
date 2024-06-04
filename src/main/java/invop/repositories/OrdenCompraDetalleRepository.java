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


}
