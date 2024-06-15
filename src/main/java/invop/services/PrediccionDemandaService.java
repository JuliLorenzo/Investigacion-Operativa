package invop.services;

import invop.entities.PrediccionDemanda;

import java.time.LocalDate;
import java.util.List;

public interface PrediccionDemandaService extends BaseService<PrediccionDemanda, Long> {
    // Métodos específicos de Prediccion Demanda

    public Integer promedioMovilPonderado(int cantidadPeriodos, List<Double> coeficientesPonderacion, Long idArticulo, LocalDate fechaPrediccion) throws Exception;
}
