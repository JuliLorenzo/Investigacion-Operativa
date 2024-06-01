package invop.services;

import invop.entities.PrediccionDemanda;
import invop.repositories.PrediccionDemandaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrediccionDemandaServiceImpl extends BaseServiceImpl<PrediccionDemanda, Long> implements PrediccionDemandaService {

    @Autowired
    private PrediccionDemandaRepository prediccionDemandaRepository;
    public PrediccionDemandaServiceImpl(PrediccionDemandaRepository prediccionDemandaRepository){
        super(prediccionDemandaRepository);
        this.prediccionDemandaRepository = prediccionDemandaRepository;
    }
}
