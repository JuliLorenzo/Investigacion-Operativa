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
            value = "SELECT DISTINCT p.nombre_proveedor " +
                    "FROM proveedores p " +
                    "JOIN proveedor_articulo pa ON pa.id_proveedor = p.id " +
                    "WHERE pa.articulo_id = :filtroArticulo " +
                    "AND p.fecha_baja IS NULL " +
                    "AND pa.fecha_baja IS NULL",
            nativeQuery = true
    )
    List<Object> findProveedoresByArticulo(@Param("filtroArticulo") String filtroArticulo);

    @Query(
            value = "SELECT DISTINCT a.nombre_articulo " +
                    "FROM articulos a " +
                    "JOIN proveedor_articulo pa ON pa.articulo_id = a.id " +
                    "WHERE pa.id_proveedor = :filtroProveedor " +
                    "AND a.fecha_baja IS NULL " +
                    "AND pa.fecha_baja IS NULL",
            nativeQuery = true
    )
    List<Object> findArticulosByProveedor(@Param("filtroProveedor") String filtroArticulo);

    @Query(
            value = "SELECT avg(tiempo_demora_articulo) " +
                    "FROM proveedor_articulo " +
                    "WHERE articulo_id = :filtroArticulo",
            nativeQuery = true
    )
    Double obtenerTiempoDemoraPromedioProveedores(@Param("filtroArticulo") String filtroArticulo);
}
