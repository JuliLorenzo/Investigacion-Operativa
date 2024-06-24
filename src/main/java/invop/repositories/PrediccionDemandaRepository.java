package invop.repositories;

import invop.entities.ErrorMetodo;
import invop.entities.PrediccionDemanda;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface PrediccionDemandaRepository extends BaseRepository<PrediccionDemanda, Long> {
    @Query(
            value = "SELECT * " +
                    "FROM predicciones_demanda " +
                    "WHERE articulo_id = :filtroArticulo ",
            nativeQuery = true
    )
    List<PrediccionDemanda> findPrediccionesByArticulo(@PathVariable("filtroArticulo") Long filtroArticulo);
}
