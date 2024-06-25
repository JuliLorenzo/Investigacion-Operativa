package invop.services;

import invop.dto.DatosPrediccionDTO;
import invop.entities.PrediccionDemanda;

import java.time.LocalDate;
import java.util.List;

public interface PrediccionDemandaService extends BaseService<PrediccionDemanda, Long> {
    public Integer predecirDemandaParaMesFuturo(DatosPrediccionDTO datosPrediccionDTO) throws Exception;
    public Integer calcularPromedioMovilPonderado(DatosPrediccionDTO datosPrediccionDTO) throws Exception;

    public Integer calculoPromedioMovilPonderado(DatosPrediccionDTO datosPrediccionDTO) throws Exception;

    public Integer calculoPromedioMovilPonderadoSuavizado(DatosPrediccionDTO datosPrediccionDTO) throws Exception;
    public Integer calcularPromedioMovilMesAnterior(Long idArticulo, LocalDate fechaPrediccion) throws Exception;
    public Integer calcularRegresionLineal(DatosPrediccionDTO datosPrediccionDTO) throws Exception;

    List<PrediccionDemanda> buscarPrediccionesSegunArticulo(Long filtroArticulo) throws Exception;

    public List<PrediccionDemanda> crearPredicciones(DatosPrediccionDTO datosPrediccion) throws Exception;
    public Integer calcularEstacional(DatosPrediccionDTO datosPrediccionDTO) throws Exception;
}
