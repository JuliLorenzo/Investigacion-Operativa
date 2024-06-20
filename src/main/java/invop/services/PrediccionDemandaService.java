package invop.services;

import invop.entities.PrediccionDemanda;

import java.time.LocalDate;
import java.util.List;

public interface PrediccionDemandaService extends BaseService<PrediccionDemanda, Long> {
    // Métodos específicos de Prediccion Demanda

    public Integer calculoPromedioMovilPonderado(int cantidadPeriodos, List<Double> coeficientesPonderacion, Long idArticulo, LocalDate fechaPrediccion) throws Exception;

    public Integer calculoPromedioMovilPonderadoSuavizado(Double alfa, LocalDate fechaPrediccion, Long idArticulo) throws Exception;
    public Integer calcularPromedioMovilMesAnterior(Long idArticulo, LocalDate fechaPrediccion) throws Exception;
    public Integer calcularRegresionLineal() throws Exception;
}
