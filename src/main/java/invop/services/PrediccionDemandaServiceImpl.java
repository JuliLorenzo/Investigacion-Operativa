package invop.services;

import invop.entities.PrediccionDemanda;
import invop.repositories.PrediccionDemandaRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class PrediccionDemandaServiceImpl extends BaseServiceImpl<PrediccionDemanda, Long> implements PrediccionDemandaService {

    @Autowired
    public PrediccionDemandaServiceImpl(PrediccionDemandaRepository prediccionDemandaRepository){
        super(prediccionDemandaRepository);
    }
}
