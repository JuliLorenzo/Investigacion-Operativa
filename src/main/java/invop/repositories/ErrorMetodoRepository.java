package invop.repositories;

import invop.entities.Articulo;
import invop.entities.ErrorMetodo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface ErrorMetodoRepository  extends BaseRepository<ErrorMetodo, Long>{
    @Query(
            value = "SELECT * " +
                    "FROM errores " +
                    "WHERE id_articulo = :filtroArticulo ",
            nativeQuery = true
    )
    List<ErrorMetodo> findErroresByArticulo(@PathVariable("filtroArticulo") Long filtroArticulo);
}
