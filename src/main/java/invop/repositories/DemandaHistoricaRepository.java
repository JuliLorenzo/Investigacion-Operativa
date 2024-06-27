package invop.repositories;


import invop.entities.DemandaHistorica;
import invop.entities.Venta;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DemandaHistoricaRepository extends BaseRepository<DemandaHistorica, Long> {
    @Query(
            value = "SELECT * " +
                    "FROM demandas_historicas " +
                    "WHERE id_articulo = :idArticulo",
            nativeQuery = true
    )
    List<DemandaHistorica> buscarDemandasHistoricasPorArticulo(@Param ("idArticulo") Long idArticulo);

}
