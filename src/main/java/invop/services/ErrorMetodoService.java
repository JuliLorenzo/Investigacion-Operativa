package invop.services;

import invop.dto.DatosErrorMetodoDto;
import invop.entities.ErrorMetodo;
import invop.enums.NombreMetodoPrediccion;

public interface ErrorMetodoService extends BaseService<ErrorMetodo, Long> {

    public ErrorMetodo crearErrorMetodo(DatosErrorMetodoDto datosError) throws Exception;
    public Double calculoError(int cantidadPeriodos, NombreMetodoPrediccion nombreMetodo, int demandaHistorica) throws Exception;
}
