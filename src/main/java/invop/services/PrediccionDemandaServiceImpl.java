package invop.services;

import invop.dto.DatosPrediccionDTO;
import invop.entities.Articulo;
import invop.entities.PrediccionDemanda;
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


    //CREAR PREDICCION
    public List<PrediccionDemanda> crearPredicciones(DatosPrediccionDTO datosPrediccion) throws Exception{
        try{
            Articulo articulo = articuloService.findArticuloById(datosPrediccion.getIdArticulo());
            List<PrediccionDemanda> listaPredicciones = new ArrayList<>();

            for(int i = 0 ; i < datosPrediccion.getCantidadPeriodos(); i++){
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
                LocalDate fechaDesde = LocalDate.of(datosPrediccion.getAnioAPredecir(), mes, 1);
                //LocalDate fechaHasta = LocalDate.of(datosPrediccion.getAnioAPredecir(), mes, fechaDesde.lengthOfMonth());

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




    // PROMEDIO MOVIL PONDERADO
    //tener en cuenta q la ponderacion es el primero de la lista con el mes mas cercano
    public Integer calculoPromedioMovilPonderado(DatosPrediccionDTO datosPrediccionDTO) throws Exception{
        try{
            // esto pq tienen q coincidir la cantidad de periodos con la cantidad de factores de ponderacion
            if(datosPrediccionDTO.getCantidadPeriodos() != datosPrediccionDTO.getCoeficientesPonderacion().size()){
                throw new IllegalArgumentException("La cantidad de periodos a utilizar debe coincidir con la cantidad de coeficientes");
            }
            double sumaValorYCoef = 0.0;
            double sumaCoef = 0.0;
            int i = 0;

            LocalDate fechaPrediccion = LocalDate.of(datosPrediccionDTO.getAnioAPredecir(), datosPrediccionDTO.getMesAPredecir(), 1);

            for(Double factorPonderacion : datosPrediccionDTO.getCoeficientesPonderacion() ){
                LocalDate fechaDesde = fechaPrediccion.minusMonths(i+1).withDayOfMonth(1);
                LocalDate fechaHasta = fechaPrediccion.minusMonths(i + 1).withDayOfMonth(fechaPrediccion.minusMonths(i + 1).lengthOfMonth());
                int demandaHistorica = demandaHistoricaService.calcularDemandaHistorica(fechaDesde, fechaHasta, datosPrediccionDTO.getIdArticulo());
                if (demandaHistorica < 0){
                    demandaHistorica = 0;
                }
                sumaValorYCoef = sumaValorYCoef + (factorPonderacion*demandaHistorica);
                sumaCoef = sumaCoef + factorPonderacion;
                i++;
            }
            //revisar el casteo a int !!!!!!!!!!!!!!!!!!!!!!!!!!
            Integer valorPrediccion = (int)(Math.ceil(sumaValorYCoef)/sumaCoef);
            return valorPrediccion;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    //PARA PROMEDIO MOVIL PONDERADO SUAVIZADO EXPONENCIALMENTE
    //calculo promedio movil a usar en el suavizado --> usa 12 meses siempre
    public Integer calcularPromedioMovilMesAnterior(Long idArticulo, LocalDate fechaPrediccion) throws Exception{
        try{
            LocalDate fechaDesde = fechaPrediccion.minusMonths(1).withDayOfMonth(1);
            LocalDate fechaHasta = fechaPrediccion.plusMonths(12).withDayOfMonth(fechaPrediccion.minusMonths(1).lengthOfMonth());
            int demandaHistorica = demandaHistoricaService.calcularDemandaHistorica(fechaDesde, fechaHasta, idArticulo);
            if(demandaHistorica <0){
                demandaHistorica = 0;
            }
            Integer valorPrediccion = demandaHistorica/12; //porque usa la anual pero ver si usamos otra
            return valorPrediccion;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }
    public Integer calculoPromedioMovilPonderadoSuavizado(DatosPrediccionDTO datosPrediccionDTO) throws Exception{
        try{
            LocalDate fechaPrediccion = LocalDate.of(datosPrediccionDTO.getAnioAPredecir(), datosPrediccionDTO.getMesAPredecir(), 1);
            LocalDate fechaDesde = fechaPrediccion.minusMonths(1).withDayOfMonth(1);
            LocalDate fechaHasta = fechaPrediccion.minusMonths(1).withDayOfMonth(fechaPrediccion.minusMonths(1).lengthOfMonth());
            int demandaHistoricaMesAnterior = demandaHistoricaService.calcularDemandaHistorica(fechaDesde, fechaHasta, datosPrediccionDTO.getIdArticulo());
            if (demandaHistoricaMesAnterior < 0){
                demandaHistoricaMesAnterior = 0;
            }
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
            int sumaDemandas = 0;
            int sumaXY = 0;
            double sumaX2 = 0;
            double a = 0.0;
            double b = 0.0;

            for(int i = 0; i < datosPrediccionDTO.getCantidadPeriodos(); i++) {
                LocalDate fechaPrediccion = LocalDate.of(datosPrediccionDTO.getAnioAPredecir(), datosPrediccionDTO.getMesAPredecir(), 1);
                LocalDate fechaDesde = fechaPrediccion.minusMonths(i + 1).withDayOfMonth(1);
                LocalDate fechaHasta = fechaPrediccion.minusMonths(i + 1).withDayOfMonth(fechaPrediccion.minusMonths(i + 1).lengthOfMonth());

                int nroMes = fechaPrediccion.minusMonths(i+1).getMonthValue(); //obtiene el nro de mes (para los x)

                int demandaHistoricaMes = demandaHistoricaService.calcularDemandaHistorica(fechaDesde, fechaHasta, datosPrediccionDTO.getIdArticulo());

                sumaXY += (nroMes * demandaHistoricaMes);
                sumaX2 += Math.pow(nroMes, 2);
                sumaDemandas += demandaHistoricaMes;
                sumaPeriodos += (nroMes);
            }

            int promedioPeriodos = sumaPeriodos / datosPrediccionDTO.getCantidadPeriodos(); // esto seria Tp o x con barrita
            int promedioDemandas = sumaDemandas / datosPrediccionDTO.getCantidadPeriodos(); // esto seria Yp o y con barrita
            double promPeriodosCuadrado = Math.pow(promedioPeriodos,2);

            b = (sumaXY - (datosPrediccionDTO.getCantidadPeriodos() * promedioPeriodos * promedioDemandas)) / (sumaX2 - (datosPrediccionDTO.getCantidadPeriodos() * promPeriodosCuadrado));
            a = promedioDemandas - (b * promedioPeriodos);

            int valorPrediccion = (int)(a + (b * mesAPredecir));

                return valorPrediccion;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    // PARA ESTACIONAL
    @Override
    public Integer calcularEstacional(DatosPrediccionDTO datosPrediccionDTO) throws Exception {
        //ArrayList<Integer> prediccionDemandaMensual = new ArrayList<Integer>();
        int cantidadMeses = 12;
        ArrayList<Integer> prediccionDemandaMensual = new ArrayList<Integer>(Collections.nCopies(cantidadMeses, 0));
        try {
            // cantidadDemandaAnualTotal la ingresa el usuario
            System.out.println();
            // int anioAPredecir = LocalDate.now().getYear();
            System.out.println("Año a predecir: " + datosPrediccionDTO.getAnioAPredecir());
            int anioActual = 0;
            int cantidadAnios = 3;
            Integer[][] demandaAnios = new Integer[cantidadAnios][cantidadMeses];
            Integer sumatoria = 0;
            for (int i = 0; i < cantidadAnios; i++) {
                anioActual = datosPrediccionDTO.getAnioAPredecir() - (i + 1);
                for (int j = 0; j < cantidadMeses; j++) {
                    System.out.println(j);
                    int mes = j + 1;
                    LocalDate fechaDesde = LocalDate.of(anioActual, mes, 1);
                    LocalDate fechaHasta = LocalDate.of(anioActual, mes, fechaDesde.lengthOfMonth());
                    Integer demandaMesActual = demandaHistoricaService.calcularDemandaHistorica(fechaDesde, fechaHasta, datosPrediccionDTO.getIdArticulo());
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
            System.out.println("demandaAnualPromedio:"+Arrays.toString(demandaAnualPromedio));
            Double[] indiceEstacionalMensual = new Double[cantidadMeses];
            for (int t = 0; t < cantidadMeses; t++) {
                int mes = t + 1;
                indiceEstacionalMensual[t] = (double) (demandaAnualPromedio[t] / demandaMensualPromedio);
                prediccionDemandaMensual.set(t, (int) Math.ceil(indiceEstacionalMensual[t] * datosPrediccionDTO.getCantidadDemandaAnualTotal()));
                PrediccionDemanda prediccionDemanda = new PrediccionDemanda();
                prediccionDemanda.setArticulo(articuloService.findById(datosPrediccionDTO.getIdArticulo()));
                prediccionDemanda.setValorPrediccion(prediccionDemandaMensual.get(t));
                prediccionDemanda.setFechaPrediccion(LocalDate.of(datosPrediccionDTO.getAnioAPredecir(), mes, 1));
                prediccionDemanda.setNombreMetodoUsado(ESTACIONALIDAD);
                prediccionDemandaRepository.save(prediccionDemanda);
                System.out.println("Llegué a guardar");
            }
            System.out.println("indiceEstacionalMensual: "+  Arrays.toString(indiceEstacionalMensual));

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        //return prediccionDemandaMensual;
        return prediccionDemandaMensual.get(datosPrediccionDTO.getMesAPredecir());
    }


    //CREAR PREDICCION SEGUN EL METODO


}
