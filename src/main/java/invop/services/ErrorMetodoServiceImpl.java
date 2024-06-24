package invop.services;

import invop.dto.DatosPrediccionDTO;
import invop.entities.Articulo;
import invop.entities.ErrorMetodo;
import invop.repositories.ErrorMetodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    public List<ErrorMetodo> buscarErroresSegunArticulo(Long filtroArticulo) throws Exception{
        try{
            List<ErrorMetodo> listaErrores = errorMetodoRepository.findErroresByArticulo(filtroArticulo);
            return listaErrores;

        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public Double calculoError(DatosPrediccionDTO datosError) throws Exception{
        try{
            double sumatoriaError = 0.0;
            List<Integer> listaDemandasHistoricas = crearListaHistoricasParaError(12, datosError.getFechaDesde(), datosError.getIdArticulo());
            int denominador = 0;

            for(int i = 0; i < 12; i++) {

                LocalDate fechaInicioMes = datosError.getFechaDesde().plusMonths(i).withDayOfMonth(1);
                LocalDate fechaFinMes = fechaInicioMes.withDayOfMonth(fechaInicioMes.lengthOfMonth());
                datosError.setMesAPredecir(datosError.getFechaDesde().plusMonths(i).getMonthValue());
                datosError.setAnioAPredecir(datosError.getAnioAPredecir());

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
                if (demandaReal > 0) {
                    double errorAbsoluto = ((double) Math.abs(demandaReal - pronosticoDemanda)) / demandaReal;

                    sumatoriaError += errorAbsoluto;
                    //contador para tener en cuenta solo los meses validos
                    denominador += 1;
                }
                System.out.println("Demanda real del mes "+ (i+1) + ": " + demandaReal);
                System.out.println("Demanda predecida del mes "+ (i+1) + ": " + pronosticoDemanda);
            }
            //esto en caso de que todas las demandas reales sean 0 (no creo q pase pero por las dudas)
            if(denominador == 0){
                denominador = 1;
            }

            double errorPorcentual = 100 * (sumatoriaError)/denominador;

            return errorPorcentual;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public ErrorMetodo crearErrorMetodo(DatosPrediccionDTO datosError) throws Exception {
        try{

            ErrorMetodo errorCalculado = new ErrorMetodo();

            int mes = datosError.getFechaDesde().getMonthValue();
            datosError.setMesAPredecir(mes);
            int anio = datosError.getFechaDesde().getYear();
            datosError.setAnioAPredecir(anio);

            Articulo articulo = articuloService.findArticuloById(datosError.getIdArticulo());
            errorCalculado.setArticulo(articulo);

            Integer demandaHistorica = demandaHistoricaService.calcularDemandaHistorica(datosError.getFechaDesde(), datosError.getFechaHasta(), datosError.getIdArticulo());
            errorCalculado.setValorDemandaReal(demandaHistorica);

            Integer prediccionDemanda = calcularSumaPredicciones(datosError);
            errorCalculado.setValorPrediccionDemanda(prediccionDemanda);

            Double errorTotal = calculoErrorTotal(datosError);
            errorCalculado.setErrorTotal(errorTotal);

            Double valorError = calculoError(datosError);
            errorCalculado.setPorcentajeError(valorError);

            errorCalculado.setNombreMetodoUsado(datosError.getNombreMetodoPrediccion());
            errorCalculado.setFechaDesde(datosError.getFechaDesde());
            errorCalculado.setFechaHasta(datosError.getFechaHasta());
            errorMetodoRepository.save(errorCalculado);

            return errorCalculado;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }
    public Double calculoErrorTotal(DatosPrediccionDTO datosError) throws Exception{
        try{
            int valorDemandaReal = demandaHistoricaService.calcularDemandaHistorica(datosError.getFechaDesde(), datosError.getFechaHasta(), datosError.getIdArticulo());

            int valorPrediccionDemanda = calcularSumaPredicciones(datosError);

            double numerador = (double)100 * Math.abs(valorDemandaReal - valorPrediccionDemanda) / valorDemandaReal;
            double valorError = numerador/12;

            return valorError;
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
                if (demandaReal < 0){
                    demandaReal = 0;
                }
                listaDemandasHistoricas.add(demandaReal);
            }

            return listaDemandasHistoricas;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }
    public Integer calcularSumaPredicciones(DatosPrediccionDTO datosError) throws Exception {
        try{

            Integer sumaPredicciones = 0;
            for (int i = 0; i < 12; i++) {
                datosError.setMesAPredecir((datosError.getFechaDesde().plusMonths(i).withDayOfMonth(1)).getMonthValue());

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

    public ErrorMetodo devolverMenorError(Long idArticulo) throws Exception{
        try{
            List<ErrorMetodo> listaErrores = buscarErroresSegunArticulo(idArticulo);
            if(listaErrores.isEmpty()){
                throw new Exception("No hay errores para el artículo proporcionado");
            }
            ErrorMetodo menorError = listaErrores.get(0);
            for(ErrorMetodo errorMetodo : listaErrores){
                if(errorMetodo.getPorcentajeError() < menorError.getPorcentajeError()){
                    menorError = errorMetodo;
                }
            }
            return menorError;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }





}

