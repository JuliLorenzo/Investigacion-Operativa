package invop.services;

import invop.entities.PrediccionDemanda;
import invop.repositories.PrediccionDemandaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
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


    // PROMEDIO MOVIL PONDERADO
    public Integer calculoPromedioMovilPonderado(int cantidadPeriodos, List<Double> coeficientesPonderacion, Long idArticulo, LocalDate fechaPrediccion) throws Exception{
        try{
            // esto pq tienen q coincidir la cantidad de periodos con el factor de ponderacion
            if(cantidadPeriodos != coeficientesPonderacion.size()){
                throw new IllegalArgumentException("La cantidad de periodos a utilizar debe coincidir con la cantidad de coeficientes");
            }
            double sumaValorYCoef = 0.0;
            double sumaCoef = 0.0;
            int i = 0;
            for(Double factorPonderacion : coeficientesPonderacion ){
                LocalDate fechaDesde = fechaPrediccion.minusMonths(i+1).withDayOfMonth(1);
                LocalDate fechaHasta = fechaPrediccion.minusMonths(i + 1).withDayOfMonth(fechaPrediccion.minusMonths(i + 1).lengthOfMonth());
                int demandaHistorica = demandaHistoricaService.calcularDemandaHistorica(fechaDesde, fechaHasta, idArticulo);

                sumaValorYCoef = sumaValorYCoef + (factorPonderacion*demandaHistorica);
                sumaCoef = sumaCoef + factorPonderacion;
                i++;
            }
            //revisar el casteo a int !!!!!!!!!!!!!!!!!!!!!!!!!!
            Integer valorPrediccion = (int)sumaValorYCoef/(int)sumaCoef;
            return valorPrediccion;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    //PARA PROMEDIO MOVIL PONDERADO SUAVIZADO EXPONENCIALMENTE
    //calculo promedio movil a usar
    public Integer calcularPromedioMovilMesAnterior(Long idArticulo, LocalDate fechaPrediccion) throws Exception{
        try{
            LocalDate fechaDesde = fechaPrediccion.minusMonths(1).withDayOfMonth(1);
            LocalDate fechaHasta = fechaPrediccion.plusMonths(12).withDayOfMonth(fechaPrediccion.minusMonths(1).lengthOfMonth());
            int demandaHistorica = demandaHistoricaService.calcularDemandaHistorica(fechaDesde, fechaHasta, idArticulo);

            Integer valorPrediccion = demandaHistorica/12; //porque usa la anual pero ver si usamos otra
            return valorPrediccion;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }
    public Integer calculoPromedioMovilPonderadoSuavizado(Double alfa, LocalDate fechaPrediccion, Long idArticulo) throws Exception{
        try{
            LocalDate fechaDesde = fechaPrediccion.minusMonths(1).withDayOfMonth(1);
            LocalDate fechaHasta = fechaPrediccion.minusMonths(1).withDayOfMonth(fechaPrediccion.minusMonths(1).lengthOfMonth());
            int demandaHistoricaMesAnterior = demandaHistoricaService.calcularDemandaHistorica(fechaDesde, fechaHasta, idArticulo);

            Integer valorPrediccionMesAnterior = calcularPromedioMovilMesAnterior(idArticulo, fechaPrediccion.minusMonths(1));

            Integer valorPrediccion = (int)(valorPrediccionMesAnterior + (alfa * (demandaHistoricaMesAnterior - valorPrediccionMesAnterior)));
            return valorPrediccion;

        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    // PARA REGRESION LINEAL
    public Integer calcularRegresionLineal(int cantPeriodosHistoricos, LocalDate fechaPrediccion, Long idArticulo) throws Exception{
        try {
            int mesAPredecir = fechaPrediccion.getMonthValue();

            int sumaPeriodos = 0;
            int sumaDemandas = 0;
            int sumaXY = 0;
            double sumaX2 = 0;
            double a = 0.0;
            double b = 0.0;

            for(int i = 0; i < cantPeriodosHistoricos; i++) {
                LocalDate fechaDesde = fechaPrediccion.minusMonths(i + 1).withDayOfMonth(1);
                LocalDate fechaHasta = fechaPrediccion.minusMonths(i + 1).withDayOfMonth(fechaPrediccion.minusMonths(i + 1).lengthOfMonth());

                int nroMes = fechaPrediccion.minusMonths(i+1).getMonthValue(); //obtiene el nro de mes (para los x)

                int demandaHistoricaMes = demandaHistoricaService.calcularDemandaHistorica(fechaDesde, fechaHasta, idArticulo);

                sumaXY += (nroMes * demandaHistoricaMes);
                sumaX2 += Math.pow(nroMes, 2);
                sumaDemandas += demandaHistoricaMes;
                sumaPeriodos += (nroMes);
            }

            int promedioPeriodos = sumaPeriodos/cantPeriodosHistoricos; // esto seria Tp o x con barrita
            int promedioDemandas = sumaDemandas/cantPeriodosHistoricos; // esto seria Yp o y con barrita
            double promPeriodosCuadrado = Math.pow(promedioPeriodos,2);

            b = (sumaXY - (cantPeriodosHistoricos * promedioPeriodos * promedioDemandas)) / (sumaX2 - (cantPeriodosHistoricos * promPeriodosCuadrado));
            a = promedioDemandas - (b * promedioPeriodos);

            int valorPrediccion = (int)(a + (b * mesAPredecir));

                return valorPrediccion;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    // PARA ESTACIONAL
    @Override
    public List<Integer> calcularEstacional(Long idArticulo, Integer cantidadDemandaAnualTotal, Integer anioAPredecir) throws Exception {
        //ArrayList<Integer> prediccionDemandaMensual = new ArrayList<Integer>();
        int cantidadMeses = 12;
        ArrayList<Integer> prediccionDemandaMensual = new ArrayList<Integer>(Collections.nCopies(cantidadMeses, 0));
        try {
            // cantidadDemandaAnualTotal la ingresa el usuario
            System.out.println();
            // int anioAPredecir = LocalDate.now().getYear();
            System.out.println("Año a predecir: " + anioAPredecir);
            int anioActual = 0;
            int cantidadAnios = 3;
            Integer[][] demandaAnios = new Integer[cantidadAnios][cantidadMeses];
            Integer sumatoria = 0;
            for (int i = 0; i < cantidadAnios; i++) {
                anioActual = anioAPredecir - (i + 1);
                for (int j = 0; j < cantidadMeses; j++) {
                    System.out.println(j);
                    int mes = j + 1;
                    LocalDate fechaDesde = LocalDate.of(anioActual, mes, 1);
                    LocalDate fechaHasta = LocalDate.of(anioActual, mes, fechaDesde.lengthOfMonth());
                    Integer demandaMesActual = demandaHistoricaService.calcularDemandaHistorica(fechaDesde, fechaHasta, idArticulo);
                    if (demandaMesActual < 0) {
                        demandaMesActual = 0;
                    }
                    System.out.println("La demanda del Mes: " + mes + " Año: " + anioActual + " fue: " + demandaMesActual);
                    demandaAnios[i][j] = demandaMesActual;
                    sumatoria = sumatoria + demandaMesActual;
                }
            }
            ;
            System.out.println("La sumatoria dio " + sumatoria);

            Integer[] demandaAnualPromedio = new Integer[cantidadMeses];
            Arrays.fill(demandaAnualPromedio, 0); // llena con 0
            Integer demandaMensualPromedio = (int) Math.ceil((double) sumatoria / (cantidadAnios * cantidadMeses));
            System.out.println("Demanda Mensual Promedio: " + demandaMensualPromedio);
            for (int k = 0; k < cantidadAnios; k++) {
                Integer sumaMes = 0;
                for (int t = 0; t < cantidadMeses; t++) {
                    sumaMes = sumaMes + demandaAnios[k][t];
                    System.out.println("Llegué hasta acá, mes: " + t + ", año: " + k);
                }
                demandaAnualPromedio[k] = (int) Math.ceil((double) sumaMes / cantidadAnios);
            }

            Double[] indiceEstacionalMensual = new Double[cantidadMeses];
            for (int t = 0; t < cantidadMeses; t++) {
                int mes = t + 1;
                indiceEstacionalMensual[t] = (double) (demandaAnualPromedio[t] / demandaMensualPromedio);
                prediccionDemandaMensual.set(t, (int) Math.ceil(indiceEstacionalMensual[t] * cantidadDemandaAnualTotal));
                PrediccionDemanda prediccionDemanda = new PrediccionDemanda();
                prediccionDemanda.setArticulo(articuloService.findById(idArticulo));
                prediccionDemanda.setValorPrediccion(prediccionDemandaMensual.get(t));
                prediccionDemanda.setFechaPrediccion(LocalDate.of(anioAPredecir, mes, 1));
                prediccionDemanda.setNombreMetodoUsado(ESTACIONALIDAD);
                prediccionDemandaRepository.save(prediccionDemanda);
                System.out.println("Llegué a guardar");
            }
            System.out.println(Arrays.toString(indiceEstacionalMensual));

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return prediccionDemandaMensual;
    }

}
