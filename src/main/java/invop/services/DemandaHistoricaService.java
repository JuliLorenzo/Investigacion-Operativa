package invop.services;

import invop.entities.DemandaHistorica;

import java.time.LocalDate;

public interface DemandaHistoricaService extends BaseService<DemandaHistorica, Long> {
    public double calcularDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo) throws Exception;
}

