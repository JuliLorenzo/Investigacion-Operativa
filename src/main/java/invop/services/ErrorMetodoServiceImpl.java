package invop.services;

import invop.entities.Articulo;
import invop.entities.DemandaHistorica;
import invop.entities.ErrorMetodo;
import invop.enums.NombreMetodoPrediccion;
import invop.repositories.ErrorMetodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import invop.dto.DatosErrorMetodoDto;

import java.time.LocalDate;

@Service
public class ErrorMetodoServiceImpl extends BaseServiceImpl<ErrorMetodo, Long> implements ErrorMetodoService {

    @Autowired
    private ErrorMetodoRepository errorMetodoRepository;

    @Autowired
    private ArticuloService articuloService;

    @Autowired
    private DemandaHistoricaService demandaHistoricaService;

    public ErrorMetodoServiceImpl(ErrorMetodoRepository errorMetodoRepository, ArticuloService articuloService, DemandaHistoricaService demandaHistoricaService) {
        super(errorMetodoRepository);
        this.errorMetodoRepository = errorMetodoRepository;
        this.articuloService = articuloService;
        this.demandaHistoricaService = demandaHistoricaService;
    }

    public Double calculoError(int cantidadPeriodos, NombreMetodoPrediccion nombreMetodo, int demandaHistorica) throws Exception{
        try{

            for(int i = 0; i < cantidadPeriodos; i++){

            }
            return 0.0; //pongo esto para q no tire error pero todavia no termino
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public ErrorMetodo crearErrorMetodo(DatosErrorMetodoDto datosError) throws Exception {
        try{
            ErrorMetodo errorCalculado = new ErrorMetodo();
            errorCalculado.setNombreMetodoUsado(datosError.getNombreMetodoPrediccion());
            errorCalculado.setFechaDesde(datosError.getFechaDesde());
            errorCalculado.setFechaHasta(datosError.getFechaHasta());

            Articulo articulo = articuloService.findArticuloById(datosError.getIdArticulo());
            errorCalculado.setArticulo(articulo);

            DemandaHistorica demandaHistorica = demandaHistoricaService.crearDemandaHistorica(datosError.getFechaDesde(), datosError.getFechaHasta(), datosError.getIdArticulo());
            errorCalculado.setDemandaReal(demandaHistorica);

            //FALTA TERMINAR: falta agregar prediccion y valor error

            return errorCalculado;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }
}
