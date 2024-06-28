package invop.services;

import invop.dto.DatosPrediccionDTO;
import invop.entities.Articulo;
import invop.entities.PrediccionDemanda;
import invop.enums.NombreMetodoPrediccion;
import invop.repositories.PrediccionDemandaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static invop.enums.NombreMetodoPrediccion.ESTACIONALIDAD;
import static java.time.LocalDate.of;

@Service
public class PrediccionDemandaServiceImpl extends BaseServiceImpl<PrediccionDemanda, Long> implements PrediccionDemandaService {

    @Autowired
    private DemandaHistoricaService demandaHistoricaService;
    @Autowired
    private PrediccionDemandaRepository prediccionDemandaRepository;
    @Autowired
    private ArticuloService articuloService;


    public PrediccionDemandaServiceImpl(PrediccionDemandaRepository prediccionDemandaRepository, DemandaHistoricaService demandaHistoricaService, ArticuloService articuloService){
        super(prediccionDemandaRepository);
        this.prediccionDemandaRepository = prediccionDemandaRepository;
        this.demandaHistoricaService = demandaHistoricaService;
        this.articuloService = articuloService;
    }

    @Override
    public List<PrediccionDemanda> buscarPrediccionesSegunArticulo(Long filtroArticulo) throws Exception{
        try{
            List<PrediccionDemanda> listaPredicciones = prediccionDemandaRepository.findPrediccionesByArticulo(filtroArticulo);
            return listaPredicciones;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    //CREAR PREDICCION
    public List<PrediccionDemanda> crearPredicciones(DatosPrediccionDTO datosPrediccion) throws Exception{
        try{
            Articulo articulo = articuloService.findArticuloById(datosPrediccion.getIdArticulo());
            List<PrediccionDemanda> listaPredicciones = new ArrayList<>();
            LocalDate fechaInicial = LocalDate.of(datosPrediccion.getAnioAPredecir(), datosPrediccion.getMesAPredecir(), 1);

            for(int i = 0 ; i < datosPrediccion.getCantidadPeriodosAdelante(); i++){
                datosPrediccion.setMesAPredecir(datosPrediccion.getMesAPredecir()+i);
                int valorPrediccion = 0;
                switch (datosPrediccion.getNombreMetodoPrediccion()){
                    case PROMEDIO_MOVIL_PONDERADO -> {
                        valorPrediccion = calculoPromedioMovilPonderado(datosPrediccion);
                    }
                    case PROMEDIO_MOVIL_SUAVIZADO -> {
                        valorPrediccion = calculoPromedioMovilPonderadoSuavizado(datosPrediccion);
                    }
                    case REGRESION_LINEAL -> {
                        valorPrediccion = calcularRegresionLineal(datosPrediccion);
                    }
                    case ESTACIONALIDAD -> {
                        valorPrediccion = calcularEstacional(datosPrediccion);
                    }
                }
                int mes = datosPrediccion.getMesAPredecir()+i;
                LocalDate fechaDesde = fechaInicial.plusMonths(i);

                PrediccionDemanda prediccionDemanda = new PrediccionDemanda();
                prediccionDemanda.setValorPrediccion(valorPrediccion);
                prediccionDemanda.setFechaPrediccion(fechaDesde);
                prediccionDemanda.setArticulo(articulo);
                prediccionDemanda.setNombreMetodoUsado(datosPrediccion.getNombreMetodoPrediccion());
                prediccionDemandaRepository.save(prediccionDemanda);

                listaPredicciones.add(prediccionDemanda);
            }
            return listaPredicciones;

        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public Integer predecirDemandaParaMesFuturo(DatosPrediccionDTO datosPrediccionDTO) throws Exception {
        switch (datosPrediccionDTO.getNombreMetodoPrediccion()) {
            case PROMEDIO_MOVIL_PONDERADO:
                return calculoPromedioMovilPonderado(datosPrediccionDTO);
            case PROMEDIO_MOVIL_SUAVIZADO:
                return calculoPromedioMovilPonderadoSuavizado(datosPrediccionDTO);
            case REGRESION_LINEAL:
                return calcularRegresionLineal(datosPrediccionDTO);
            case ESTACIONALIDAD:
                return calcularEstacional(datosPrediccionDTO);
            default:
                throw new IllegalArgumentException("Método de predicción no soportado: " + datosPrediccionDTO.getNombreMetodoPrediccion());
        }
    }


    public Integer calcularPromedioMovilMesAnterior(Long idArticulo, LocalDate fechaPrediccion) throws Exception{
        try {
            LocalDate fechaDesde = fechaPrediccion.minusMonths(12).withDayOfMonth(1);
            LocalDate fechaHasta = fechaPrediccion.minusMonths(1).withDayOfMonth(fechaPrediccion.minusMonths(1).lengthOfMonth());
            int demandaHistorica = demandaHistoricaService.calcularDemandaHistorica(fechaDesde, fechaHasta, idArticulo);
            if(demandaHistorica <0){
                demandaHistorica = 0;
            }
            Integer valorPrediccion = demandaHistorica/12;
            return valorPrediccion;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Integer calculoPromedioMovilPonderado(DatosPrediccionDTO datosPrediccionDTO) throws Exception {
        try {
            if (datosPrediccionDTO.getCantidadPeriodosHistoricos() != datosPrediccionDTO.getCoeficientesPonderacion().size()) {
                throw new IllegalArgumentException("La cantidad de periodos a utilizar debe coincidir con la cantidad de coeficientes");
            }

            double sumaValorYCoef = 0.0;
            double sumaCoef = 0.0;
            int i = 0;

            LocalDate fechaPrediccion = LocalDate.of(datosPrediccionDTO.getAnioAPredecir(), datosPrediccionDTO.getMesAPredecir(), 1);

            for (Double factorPonderacion : datosPrediccionDTO.getCoeficientesPonderacion()) {
                LocalDate fechaDesde = fechaPrediccion.minusMonths(i + 1).withDayOfMonth(1);
                LocalDate fechaHasta = fechaPrediccion.minusMonths(i + 1).withDayOfMonth(fechaPrediccion.minusMonths(i + 1).lengthOfMonth());

                int demandaHistorica = demandaHistoricaService.obtenerDemandaHistoricaOProyectada(fechaDesde, fechaHasta, datosPrediccionDTO.getIdArticulo());

                if (demandaHistorica == -1) {
                    // Crear una copia del DTO con algunos valores cambiados (mes, fechaDesde, fechaHasta) para la predicción
                    DatosPrediccionDTO prediccionDTO = new DatosPrediccionDTO();
                    prediccionDTO.setCantidadPeriodosHistoricos(datosPrediccionDTO.getCantidadPeriodosHistoricos());
                    prediccionDTO.setCantidadPeriodosAdelante(datosPrediccionDTO.getCantidadPeriodosAdelante());
                    prediccionDTO.setCoeficientesPonderacion(datosPrediccionDTO.getCoeficientesPonderacion());
                    prediccionDTO.setIdArticulo(datosPrediccionDTO.getIdArticulo());
                    prediccionDTO.setAnioAPredecir(fechaPrediccion.minusMonths(i + 1).getYear());
                    prediccionDTO.setMesAPredecir(fechaPrediccion.minusMonths(i + 1).getMonthValue());
                    prediccionDTO.setAlfa(datosPrediccionDTO.getAlfa());
                    prediccionDTO.setNombreMetodoPrediccion(NombreMetodoPrediccion.PROMEDIO_MOVIL_PONDERADO);

                    // Predecir la demanda usando la copia del DTO
                    demandaHistorica = predecirDemandaParaMesFuturo(prediccionDTO);
                }

                sumaValorYCoef += factorPonderacion * demandaHistorica;
                sumaCoef += factorPonderacion;
                i++;
            }

            // Calcular y retornar el valor predicho
            Integer valorPrediccion = (int) Math.ceil(sumaValorYCoef / sumaCoef);
            return valorPrediccion;

        } catch (Exception e) {
            throw new Exception("Error al calcular el promedio móvil ponderado: " + e.getMessage());
        }
    }

    public Integer calculoPromedioMovilPonderadoSuavizado(DatosPrediccionDTO datosPrediccionDTO) throws Exception{
        try{
            LocalDate fechaPrediccion = LocalDate.of(datosPrediccionDTO.getAnioAPredecir(), datosPrediccionDTO.getMesAPredecir(), 1);
            LocalDate fechaDesde = fechaPrediccion.minusMonths(1).withDayOfMonth(1);
            LocalDate fechaHasta = fechaPrediccion.minusMonths(1).withDayOfMonth(fechaPrediccion.minusMonths(1).lengthOfMonth());

            int demandaHistoricaMesAnterior = demandaHistoricaService.obtenerDemandaHistoricaOProyectada(fechaDesde, fechaHasta, datosPrediccionDTO.getIdArticulo());

            if (demandaHistoricaMesAnterior == -1) {
                // Crear una copia del DTO con algunos valores cambiados (mes, fechaDesde, fechaHasta) para la predicción
                DatosPrediccionDTO prediccionDTO = new DatosPrediccionDTO();
                prediccionDTO.setCantidadPeriodosHistoricos(datosPrediccionDTO.getCantidadPeriodosHistoricos());
                prediccionDTO.setCantidadPeriodosAdelante(datosPrediccionDTO.getCantidadPeriodosAdelante());
                prediccionDTO.setCoeficientesPonderacion(datosPrediccionDTO.getCoeficientesPonderacion());
                prediccionDTO.setIdArticulo(datosPrediccionDTO.getIdArticulo());
                prediccionDTO.setAnioAPredecir(fechaPrediccion.minusMonths(1).getYear());
                prediccionDTO.setMesAPredecir(fechaPrediccion.minusMonths(1).getMonthValue());
                prediccionDTO.setAlfa(datosPrediccionDTO.getAlfa());
                prediccionDTO.setNombreMetodoPrediccion(NombreMetodoPrediccion.PROMEDIO_MOVIL_SUAVIZADO);

                // Predecir la demanda usando la copia del DTO
                demandaHistoricaMesAnterior = predecirDemandaParaMesFuturo(prediccionDTO);
            }
            System.out.println("La demanda historica del ems anterior es: "+ demandaHistoricaMesAnterior);
            Integer valorPrediccionMesAnterior = calcularPromedioMovilMesAnterior(datosPrediccionDTO.getIdArticulo(), fechaPrediccion.minusMonths(1));

            Integer valorPrediccion = (int)(valorPrediccionMesAnterior + (datosPrediccionDTO.getAlfa() * (demandaHistoricaMesAnterior - valorPrediccionMesAnterior)));
            return valorPrediccion;

        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    // PARA REGRESION LINEAL
    public Integer calcularRegresionLineal(DatosPrediccionDTO datosPrediccionDTO) throws Exception{
        try {
            int mesAPredecir = datosPrediccionDTO.getMesAPredecir();

            int sumaPeriodos = 0;
            Double sumaDemandas = 0.0;
            int sumaXY = 0;
            double sumaX2 = 0;
            double a = 0.0;
            double b = 0.0;
            LocalDate fechaPrediccion = LocalDate.of(datosPrediccionDTO.getAnioAPredecir(), datosPrediccionDTO.getMesAPredecir(), 1);

            for(int i = 0; i < datosPrediccionDTO.getCantidadPeriodosHistoricos(); i++) {
                LocalDate fechaDesde = fechaPrediccion.minusMonths(i + 1).withDayOfMonth(1);
                LocalDate fechaHasta = fechaPrediccion.minusMonths(i + 1).withDayOfMonth(fechaPrediccion.minusMonths(i + 1).lengthOfMonth());

                int nroMes = datosPrediccionDTO.getCantidadPeriodosHistoricos() - i;

                int demandaHistoricaMes = demandaHistoricaService.obtenerDemandaHistoricaOProyectada(fechaDesde, fechaHasta, datosPrediccionDTO.getIdArticulo());

                if (demandaHistoricaMes == -1) {
                    // Crear una copia del DTO con algunos valores cambiados (mes, fechaDesde, fechaHasta) para la predicción
                    DatosPrediccionDTO prediccionDTO = new DatosPrediccionDTO();
                    prediccionDTO.setCantidadPeriodosHistoricos(datosPrediccionDTO.getCantidadPeriodosHistoricos());
                    prediccionDTO.setCantidadPeriodosAdelante(datosPrediccionDTO.getCantidadPeriodosAdelante());
                    prediccionDTO.setCoeficientesPonderacion(datosPrediccionDTO.getCoeficientesPonderacion());
                    prediccionDTO.setIdArticulo(datosPrediccionDTO.getIdArticulo());
                    prediccionDTO.setAnioAPredecir(fechaPrediccion.minusMonths(1).getYear());
                    prediccionDTO.setMesAPredecir(fechaPrediccion.minusMonths(1).getMonthValue());
                    prediccionDTO.setAlfa(datosPrediccionDTO.getAlfa());
                    prediccionDTO.setNombreMetodoPrediccion(NombreMetodoPrediccion.REGRESION_LINEAL);

                    // Predecir la demanda usando la copia del DTO
                    demandaHistoricaMes = predecirDemandaParaMesFuturo(prediccionDTO);
                }

                sumaXY += (nroMes * demandaHistoricaMes);

                sumaX2 += Math.pow(nroMes, 2);

                sumaDemandas += demandaHistoricaMes;
                sumaPeriodos += (nroMes);
            }

            Double promedioPeriodos = (double) (sumaPeriodos / datosPrediccionDTO.getCantidadPeriodosHistoricos()); // esto seria Tp o x con barrita
            Double promedioDemandas =  (sumaDemandas / datosPrediccionDTO.getCantidadPeriodosHistoricos()); // esto seria Yp o y con barrita

            double promPeriodosCuadrado = Math.pow(promedioPeriodos,2);

            b = (sumaXY - (datosPrediccionDTO.getCantidadPeriodosHistoricos() * promedioPeriodos * promedioDemandas)) / (sumaX2 - (datosPrediccionDTO.getCantidadPeriodosHistoricos() * promPeriodosCuadrado));
            a = promedioDemandas - (b * promedioPeriodos);

            Integer valorPrediccion = (int)(a + (b * (datosPrediccionDTO.getCantidadPeriodosHistoricos()+1)));

            return valorPrediccion;
        } catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    // PARA ESTACIONAL
    @Override
    public Integer calcularEstacional(DatosPrediccionDTO datosPrediccionDTO) throws Exception {
        int cantidadMeses = 12;
        ArrayList<Integer> prediccionDemandaMensual = new ArrayList<Integer>(Collections.nCopies(cantidadMeses, 0));
        try {
            int anioActual = 0;
            int cantidadAnios = 3;
            Integer[][] demandaAnios = new Integer[cantidadAnios][cantidadMeses];
            Integer sumatoria = 0;
            for (int i = 0; i < cantidadAnios; i++) {
                anioActual = datosPrediccionDTO.getAnioAPredecir() - (i + 1);
                for (int j = 0; j < cantidadMeses; j++) {
                    int mes = j + 1;
                    LocalDate fechaDesde = LocalDate.of(anioActual, mes, 1);
                    LocalDate fechaHasta = LocalDate.of(anioActual, mes, fechaDesde.lengthOfMonth());
                    Integer demandaMesActual = demandaHistoricaService.calcularDemandaHistorica(fechaDesde, fechaHasta, datosPrediccionDTO.getIdArticulo());

                    demandaAnios[i][j] = demandaMesActual;
                    sumatoria = sumatoria + demandaMesActual;
                }
            }
            
            Double[] demandaAnualPromedio = new Double[cantidadMeses];
            Arrays.fill(demandaAnualPromedio, 0.0); // llena con 0
            Double demandaMensualPromedio = Double.valueOf((double) sumatoria / (cantidadAnios * cantidadMeses));

            for (int t = 0; t < cantidadMeses; t++) {
                Integer sumaMes = 0;
                for (int k = 0; k < cantidadAnios; k++) {
                    sumaMes = sumaMes + demandaAnios[k][t];
                }
                demandaAnualPromedio[t] = Double.valueOf((double) sumaMes / cantidadAnios);
            }
            Double[] indiceEstacionalMensual = new Double[cantidadMeses];
            for (int t = 0; t < cantidadMeses; t++) {
                indiceEstacionalMensual[t] = demandaAnualPromedio[t] / demandaMensualPromedio;
                prediccionDemandaMensual.set(t, (int) Math.ceil(indiceEstacionalMensual[t] * datosPrediccionDTO.getCantidadDemandaAnualTotal()/cantidadMeses));
            }
            return prediccionDemandaMensual.get(datosPrediccionDTO.getMesAPredecir()-1);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}
