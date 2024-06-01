package invop.services;

import invop.entities.DemandaHistorica;
import invop.repositories.DemandaHistoricaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DemandaHistoricaServiceImpl extends BaseServiceImpl<DemandaHistorica, Long> implements DemandaHistoricaService {

    @Autowired
    private DemandaHistoricaRepository demandaHistoricaRepository;
    public DemandaHistoricaServiceImpl(DemandaHistoricaRepository demandaHistoricaRepository){
        super(demandaHistoricaRepository);
        this.demandaHistoricaRepository = demandaHistoricaRepository;
    }

    public void calcularDemandaHistorica(Date fechaDesde, Date fechaHasta, Long idArticulo){}

}

