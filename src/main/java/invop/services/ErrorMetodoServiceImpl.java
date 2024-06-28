package invop.services;

import invop.dto.DatosPrediccionDTO;
import invop.entities.Articulo;
import invop.entities.ErrorMetodo;
import invop.enums.NombreMetodoPrediccion;
import invop.repositories.ErrorMetodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

            int denominador = 0;
            long cantidadMeses = ChronoUnit.MONTHS.between(datosError.getFechaDesde(), datosError.getFechaHasta());
            List<Integer> listaDemandasHistoricas = crearListaHistoricasParaError(cantidadMeses, datosError.getFechaDesde(), datosError.getIdArticulo());

            System.out.println("La cantidad de meses de la fecha ingersada es: "+cantidadMeses);
            for(int i = 0; i < cantidadMeses; i++) {

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
            System.out.println("La sumatoria es: " + sumatoriaError);
            System.out.println("El denominador es: " + denominador);
            double errorPorcentual = 100 * (sumatoriaError)/denominador;

            return errorPorcentual;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public ErrorMetodo crearErrorMetodoPMP(DatosPrediccionDTO datosError) throws Exception{
        try{
            System.out.println("ENTRE AL CREAR ERROR DEL PMP");
            datosError.setNombreMetodoPrediccion(NombreMetodoPrediccion.PROMEDIO_MOVIL_PONDERADO);

            ErrorMetodo errorPMP = new ErrorMetodo();

            Articulo articulo = articuloService.findArticuloById(datosError.getIdArticulo());
            errorPMP.setArticulo(articulo);

            Integer demandaHistorica = demandaHistoricaService.calcularDemandaHistorica(datosError.getFechaDesde(), datosError.getFechaHasta(), datosError.getIdArticulo());
            errorPMP.setValorDemandaReal(demandaHistorica);

            Integer prediccionDemanda = calcularSumaPredicciones(datosError);
            errorPMP.setValorPrediccionDemanda(prediccionDemanda);

            Double errorTotal = calculoErrorTotal(datosError);
            errorPMP.setErrorTotal(errorTotal);

            Double valorError = calculoError(datosError);
            errorPMP.setPorcentajeError(valorError);

            errorPMP.setNombreMetodoUsado(datosError.getNombreMetodoPrediccion());
            errorPMP.setFechaDesde(datosError.getFechaDesde());
            errorPMP.setFechaHasta(datosError.getFechaHasta());
            errorMetodoRepository.save(errorPMP);

            return errorPMP;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public ErrorMetodo crearErrorMetodoPMPS(DatosPrediccionDTO datosError) throws Exception{
        try{
            System.out.println("ENTRE AL CREAR ERROR DEL PMPS");
            datosError.setNombreMetodoPrediccion(NombreMetodoPrediccion.PROMEDIO_MOVIL_SUAVIZADO);

            ErrorMetodo errorPMPS = new ErrorMetodo();

            Articulo articulo = articuloService.findArticuloById(datosError.getIdArticulo());
            errorPMPS.setArticulo(articulo);

            Integer demandaHistorica = demandaHistoricaService.calcularDemandaHistorica(datosError.getFechaDesde(), datosError.getFechaHasta(), datosError.getIdArticulo());
            errorPMPS.setValorDemandaReal(demandaHistorica);

            Integer prediccionDemanda = calcularSumaPredicciones(datosError);
            errorPMPS.setValorPrediccionDemanda(prediccionDemanda);

            Double errorTotal = calculoErrorTotal(datosError);
            errorPMPS.setErrorTotal(errorTotal);

            Double valorError = calculoError(datosError);
            errorPMPS.setPorcentajeError(valorError);

            errorPMPS.setNombreMetodoUsado(datosError.getNombreMetodoPrediccion());
            errorPMPS.setFechaDesde(datosError.getFechaDesde());
            errorPMPS.setFechaHasta(datosError.getFechaHasta());
            errorMetodoRepository.save(errorPMPS);

            return errorPMPS;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public ErrorMetodo crearErrorMetodoRL(DatosPrediccionDTO datosError) throws Exception{
        try{
            System.out.println("ENTRE AL CREAR ERROR DEL REGRESION LINEAL");
            datosError.setNombreMetodoPrediccion(NombreMetodoPrediccion.REGRESION_LINEAL);

            ErrorMetodo errorRL = new ErrorMetodo();

            Articulo articulo = articuloService.findArticuloById(datosError.getIdArticulo());
            errorRL.setArticulo(articulo);

            Integer demandaHistorica = demandaHistoricaService.calcularDemandaHistorica(datosError.getFechaDesde(), datosError.getFechaHasta(), datosError.getIdArticulo());
            errorRL.setValorDemandaReal(demandaHistorica);

            Integer prediccionDemanda = calcularSumaPredicciones(datosError);
            errorRL.setValorPrediccionDemanda(prediccionDemanda);

            Double errorTotal = calculoErrorTotal(datosError);
            errorRL.setErrorTotal(errorTotal);

            Double valorError = calculoError(datosError);
            errorRL.setPorcentajeError(valorError);

            errorRL.setNombreMetodoUsado(datosError.getNombreMetodoPrediccion());
            errorRL.setFechaDesde(datosError.getFechaDesde());
            errorRL.setFechaHasta(datosError.getFechaHasta());
            errorMetodoRepository.save(errorRL);

            return errorRL;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public ErrorMetodo crearErrorMetodoEst(DatosPrediccionDTO datosError) throws Exception{
        try{
            System.out.println("ENTRE AL CREAR ERROR DEL ESTACIONAL");
            datosError.setNombreMetodoPrediccion(NombreMetodoPrediccion.ESTACIONALIDAD);

            ErrorMetodo errorEst = new ErrorMetodo();

            Articulo articulo = articuloService.findArticuloById(datosError.getIdArticulo());
            errorEst.setArticulo(articulo);

            Integer demandaHistorica = demandaHistoricaService.calcularDemandaHistorica(datosError.getFechaDesde(), datosError.getFechaHasta(), datosError.getIdArticulo());
            errorEst.setValorDemandaReal(demandaHistorica);

            Integer prediccionDemanda = calcularSumaPredicciones(datosError);
            errorEst.setValorPrediccionDemanda(prediccionDemanda);

            Double errorTotal = calculoErrorTotal(datosError);
            errorEst.setErrorTotal(errorTotal);

            Double valorError = calculoError(datosError);
            errorEst.setPorcentajeError(valorError);

            errorEst.setNombreMetodoUsado(datosError.getNombreMetodoPrediccion());
            errorEst.setFechaDesde(datosError.getFechaDesde());
            errorEst.setFechaHasta(datosError.getFechaHasta());
            errorMetodoRepository.save(errorEst);

            return errorEst;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }
    public List<ErrorMetodo> crearErroresMetodos(DatosPrediccionDTO datosError) throws Exception {
        try{

            List<ErrorMetodo> listaErrores = new ArrayList<>();

            int mes = datosError.getFechaDesde().getMonthValue();
            datosError.setMesAPredecir(mes);
            int anio = datosError.getFechaDesde().getYear();
            datosError.setAnioAPredecir(anio);

            ErrorMetodo errorPMP = crearErrorMetodoPMP(datosError);
            listaErrores.add(errorPMP);

            ErrorMetodo errorPMPS = crearErrorMetodoPMPS(datosError);
            listaErrores.add(errorPMPS);

            ErrorMetodo errorRL = crearErrorMetodoRL(datosError);
            listaErrores.add(errorRL);

            ErrorMetodo errorEst = crearErrorMetodoEst(datosError);
            listaErrores.add(errorEst);

            ErrorMetodo menorError = devolverMenorError(datosError.getIdArticulo());
            NombreMetodoPrediccion metodoPredeterminado = menorError.getNombreMetodoUsado();

            Articulo articulo = articuloService.findArticuloById(datosError.getIdArticulo());
            articulo.setMetodoPrediccionPredeterminado(metodoPredeterminado);
            articuloService.update(datosError.getIdArticulo(), articulo);

            return listaErrores;
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

    public List<Integer> crearListaHistoricasParaError(long cantPeriodos, LocalDate fechaDesde, Long idArticulo) throws Exception {
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

