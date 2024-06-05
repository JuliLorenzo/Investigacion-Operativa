package invop.repositories;

import invop.entities.OrdenCompra;
import invop.entities.ProveedorArticulo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProveedorArticuloRepository extends BaseRepository<ProveedorArticulo, Long> {
    @Query(
            value = "SELECT p.nombre_proveedor " +
                    "FROM proveedores p " +
                    "JOIN proveedor_articulo pa ON pa.id_proveedor = p.id " +
                    "WHERE pa.articulo_id like %:filtroArticulo% " +
                    "AND p.fecha_baja IS NULL " +
                    "AND pa.fecha_baja IS NULL",
            nativeQuery = true
    )
    List<Object> findProveedoresByArticulo(@Param("filtroArticulo") String filtroArticulo);
}
