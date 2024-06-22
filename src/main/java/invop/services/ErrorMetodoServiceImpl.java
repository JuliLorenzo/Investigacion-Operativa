package invop.services;

import invop.dto.DatosPrediccionDTO;
import invop.entities.Articulo;
import invop.entities.DemandaHistorica;
import invop.entities.ErrorMetodo;
import invop.repositories.ErrorMetodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ErrorMetodoServiceImpl extends BaseServiceImpl<ErrorMetodo, Long> implements ErrorMetodoService {

    @Autowired
    private ErrorMetodoRepository errorMetodoRepository;

    @Autowired
    private ArticuloService articuloService;

    @Autowired
    private PrediccionDemandaService prediccionDemandaService;

    @Autowired
    private DemandaHistoricaService demandaHistoricaService;

    public ErrorMetodoServiceImpl(ErrorMetodoRepository errorMetodoRepository, ArticuloService articuloService, DemandaHistoricaService demandaHistoricaService, PrediccionDemandaService prediccionDemandaService) {
        super(errorMetodoRepository);
        this.errorMetodoRepository = errorMetodoRepository;
        this.articuloService = articuloService;
        this.demandaHistoricaService = demandaHistoricaService;
        this.prediccionDemandaService = prediccionDemandaService;
    }

    public Integer calcularSumaPredicciones(DatosPrediccionDTO datosError) throws Exception {
        try{
            Integer sumaPredicciones = 0;
            for (int i = 0; i < 12; i++) {

                LocalDate fechaInicioMes = datosError.getFechaDesde().plusMonths(i).withDayOfMonth(1);

                int pronosticoDemanda = 0;
                switch (datosError.getNombreMetodoPrediccion()) {
                    case PROMEDIO_MOVIL_SUAVIZADO -> {
                        pronosticoDemanda = prediccionDemandaService.calculoPromedioMovilPonderadoSuavizado(datosError);
                    }
                    case PROMEDIO_MOVIL_PONDERADO -> {
                        pronosticoDemanda = prediccionDemandaService.calculoPromedioMovilPonderado(datosError);
                    }
                    case REGRESION_LINEAL -> {
                        pronosticoDemanda = prediccionDemandaService.calcularRegresionLineal(datosError);
                    }
                    case ESTACIONALIDAD -> {
                        pronosticoDemanda = prediccionDemandaService.calcularEstacional(datosError);
                    }
                    default -> throw new IllegalArgumentException("Metodo de predicción no válido: " + datosError.getNombreMetodoPrediccion());
                }
                sumaPredicciones += pronosticoDemanda;
            }
            return sumaPredicciones;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }
    public Double calculoError(DatosPrediccionDTO datosError) throws Exception{
        try{
            double sumatoriaError = 0.0;
            List<Integer> listaDemandasHistoricas = crearListaHistoricasParaError(12, datosError.getFechaDesde(), datosError.getIdArticulo());

            for(int i = 0; i < 12; i++){

                LocalDate fechaInicioMes = datosError.getFechaDesde().plusMonths(i).withDayOfMonth(1);
                LocalDate fechaFinMes = fechaInicioMes.withDayOfMonth(fechaInicioMes.lengthOfMonth());

                //calcular pronostico demanda
                int pronosticoDemanda = 0;
                switch (datosError.getNombreMetodoPrediccion()) {
                    case PROMEDIO_MOVIL_SUAVIZADO -> {
                        pronosticoDemanda = prediccionDemandaService.calculoPromedioMovilPonderadoSuavizado(datosError);

                    }
                    case PROMEDIO_MOVIL_PONDERADO -> {
                        pronosticoDemanda = prediccionDemandaService.calculoPromedioMovilPonderado(datosError);

                    }
                    case REGRESION_LINEAL -> {
                        pronosticoDemanda = prediccionDemandaService.calcularRegresionLineal(datosError);

                    }
                    case ESTACIONALIDAD -> {
                        pronosticoDemanda = prediccionDemandaService.calcularEstacional(datosError);

                    }
                    default -> throw new IllegalArgumentException("Metodo de predicción no válido: " + datosError.getNombreMetodoPrediccion());
                }

                int demandaReal = listaDemandasHistoricas.get(i);

                double errorAbsoluto = ((double)Math.abs(demandaReal - pronosticoDemanda)) / demandaReal;

                sumatoriaError += errorAbsoluto;
            }

            double errorPorcentual = 100 * (sumatoriaError)/12;

            return errorPorcentual;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public ErrorMetodo crearErrorMetodo(DatosPrediccionDTO datosError) throws Exception {
        try{
            ErrorMetodo errorCalculado = new ErrorMetodo();
            errorCalculado.setNombreMetodoUsado(datosError.getNombreMetodoPrediccion());
            errorCalculado.setFechaDesde(datosError.getFechaDesde());
            errorCalculado.setFechaHasta(datosError.getFechaHasta());

            Articulo articulo = articuloService.findArticuloById(datosError.getIdArticulo());
            errorCalculado.setArticulo(articulo);

            Integer demandaHistorica = demandaHistoricaService.calcularDemandaHistorica(datosError.getFechaDesde(), datosError.getFechaHasta(), datosError.getIdArticulo());
            errorCalculado.setValorDemandaReal(demandaHistorica);

            Double porcentajeError = calculoError(datosError);
            errorCalculado.setPorcentajeError(porcentajeError);

            Integer prediccionDemanda = calcularSumaPredicciones(datosError);
            errorCalculado.setValorPrediccionDemanda(prediccionDemanda);

            Double valorError = calculoError(datosError);
            errorCalculado.setPorcentajeError(valorError);

            errorMetodoRepository.save(errorCalculado);

            return errorCalculado;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public List<Integer> crearListaHistoricasParaError(int cantPeriodos, LocalDate fechaDesde, Long idArticulo) throws Exception {
        try{
            List<Integer> listaDemandasHistoricas = new ArrayList<>();

            for (int i = 0; i < cantPeriodos; i++) {
                LocalDate fechaInicioMes = fechaDesde.plusMonths(i).withDayOfMonth(1);
                LocalDate fechaFinMes = fechaInicioMes.withDayOfMonth(fechaInicioMes.lengthOfMonth());
                int demandaReal = demandaHistoricaService.calcularDemandaHistorica(fechaInicioMes, fechaFinMes, idArticulo);
                listaDemandasHistoricas.add(demandaReal);
            }

            return listaDemandasHistoricas;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }


}

