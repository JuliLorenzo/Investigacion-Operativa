package invop.repositorios;

import invop.entities.Base;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepositorio <E extends Base, ID extends Serializable> extends JpaRepository<E, ID> {
}
