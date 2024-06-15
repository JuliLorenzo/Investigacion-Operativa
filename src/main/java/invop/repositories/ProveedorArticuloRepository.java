package invop.repositories;

import invop.entities.OrdenCompra;
import invop.entities.ProveedorArticulo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface ProveedorArticuloRepository extends BaseRepository<ProveedorArticulo, Long> {
    @Query(
            value = "SELECT DISTINCT p.nombre_proveedor " +
                    "FROM proveedores p " +
                    "JOIN proveedor_articulo pa ON pa.id_proveedor = p.id " +
                    "WHERE pa.articulo_id = :filtroArticulo",
            nativeQuery = true
    )
    List<ProveedorArticulo> findProveedoresByArticulo(@PathVariable("filtroArticulo") Long filtroArticulo);

    @Query(
            value = "SELECT DISTINCT a.nombre_articulo " +
                    "FROM articulos a " +
                    "JOIN proveedor_articulo pa ON pa.articulo_id = a.id " +
                    "WHERE pa.id_proveedor = :filtroProveedor",
            nativeQuery = true
    )
    List<ProveedorArticulo> findArticulosByProveedor(@Param("filtroProveedor") Long filtroArticulo);

    @Query(
            value = "SELECT costo_pedido " +
                    "FROM proveedor_articulo pa " +
                    "WHERE pa.articulo_id = :filtroArticulo" +
                    "AND pa.id_proveedor = :filtroProveedor",
            nativeQuery = true
    )
    Double findCostoPedidoByArticuloAndProveedor(@Param("filtroArticulo") Long filtroArticulo, @Param("filtroProveedor") Long filtroProveedor);

    @Query(
            value = "SELECT tiempo_demora_articulo " +
                    "FROM proveedor_articulo pa " +
                    "WHERE pa.articulo_id = :filtroArticulo" +
                    "AND pa.id_proveedor = :filtroProveedor",
            nativeQuery = true
    )
    Double findTiempoDemoraArticuloByArticuloAndProveedor(@Param("filtroArticulo") Long filtroArticulo, @Param("filtroProveedor") Long filtroProveedor);

    @Query(
            value = "SELECT avg(tiempo_demora_articulo) " +
                    "FROM proveedor_articulo " +
                    "WHERE articulo_id = :filtroArticulo",
            nativeQuery = true
    )
    Double obtenerTiempoDemoraPromedioProveedores(@Param("filtroArticulo") Long filtroArticulo);
}
