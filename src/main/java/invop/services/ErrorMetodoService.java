package invop.services;

import invop.dto.DatosPrediccionDTO;
import invop.entities.ErrorMetodo;
import invop.enums.NombreMetodoPrediccion;

import java.time.LocalDate;
import java.util.List;

public interface ErrorMetodoService extends BaseService<ErrorMetodo, Long> {

    public ErrorMetodo crearErrorMetodo(DatosPrediccionDTO datosError) throws Exception;
    public Double calculoError(int cantidadPeriodos, NombreMetodoPrediccion nombreMetodo, LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo) throws Exception;

    public Integer calcularSumaPredicciones(DatosPrediccionDTO datosError) throws Exception;
    public List<Integer> crearListaHistoricasParaError(int cantPeriodos, LocalDate fechaDesde, Long idArticulo) throws Exception;

}
