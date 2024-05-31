package invop.services;

import invop.entities.DemandaHistorica;
import invop.repositories.DemandaHistoricaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemandaHistoricaImpl extends BaseServiceImpl<DemandaHistorica, Long> implements DemandaHistoricaService {

    @Autowired
    public DemandaHistoricaImpl(DemandaHistoricaRepository demandaHistoricaRepository){
        super(demandaHistoricaRepository);
    }

}

