package invop.repositories;

import invop.entities.*;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface ArticuloRepository extends BaseRepository<Articulo, Long> {

    @Modifying
    @Transactional
    @Query(
            value = "DELETE " +
                    "FROM demandas_historicas " +
                    "WHERE id_articulo = :idArticulo",
            nativeQuery = true
    )
    int deleteDemandasHistoricasPorArticulo(@Param("idArticulo") Long idArticulo);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE " +
                    "FROM orden_compra_detalles " +
                    "WHERE id_articulo = :idArticulo ",
            nativeQuery = true
    )
    int deleteDetallesPorArticulo(@Param("idArticulo") Long idArticulo);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE " +
                    "FROM predicciones_demanda " +
                    "WHERE articulo_id = :idArticulo ",
            nativeQuery = true
    )
    int deletePrediccionesByArticulo(@Param("idArticulo") Long idArticulo);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE " +
                    "FROM proveedor_articulo  " +
                    "WHERE articulo_id = :idArticulo",
            nativeQuery = true
    )
    int deleteProveedorArticuloByArticulo(@Param("idArticulo") Long idArticulo);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE " +
                    "FROM venta_detalles " +
                    "WHERE id_articulo = :idArticulo ",
            nativeQuery = true
    )
    int deleteDetallesByVenta(@Param("idArticulo") Long idArticulo);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE " +
                    "FROM errores " +
                    "WHERE id_articulo = :idArticulo ",
            nativeQuery = true
    )
    int deleteErroresByArticulo(@Param("idArticulo") Long idArticulo);

}