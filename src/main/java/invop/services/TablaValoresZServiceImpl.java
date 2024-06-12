package invop.services;

import invop.entities.TablaValoresZ;
import invop.repositories.BaseRepository;
import invop.repositories.TablaValoresZRepository;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TablaValoresZServiceImpl extends BaseServiceImpl<TablaValoresZ, Long> implements TablaValoresZService{

    @Autowired
    private TablaValoresZRepository tablaValoresZRepository;
    public TablaValoresZServiceImpl(BaseRepository<TablaValoresZ, Long> baseRepository, TablaValoresZRepository tablaValoresZRepository) {
        super(baseRepository);
        this.tablaValoresZRepository = tablaValoresZRepository;
    }
}
