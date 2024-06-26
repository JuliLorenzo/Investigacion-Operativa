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
            /*if( articulo.getMetodoPrediccionPredeterminado() == null){
                throw new Exception("El artículo no tiene un método de predicción predeterminado.");
            }
            datosPrediccion.setNombreMetodoPrediccion(articulo.getMetodoPrediccionPredeterminado());*/
            List<PrediccionDemanda> listaPredicciones = new ArrayList<>();
            LocalDate fechaInicial = LocalDate.of(datosPrediccion.getAnioAPredecir(), datosPrediccion.getMesAPredecir(), 1);

            System.out.println("El metodo de prediccion es: "+datosPrediccion.getNombreMetodoPrediccion());
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
                System.out.println("EL VALOR DE LA PREDICCION ESSSSSSSS: "+valorPrediccion);
                int mes = datosPrediccion.getMesAPredecir()+i;
                //LocalDate fechaDesde = datosPrediccion.getFechaDesde().plusMonths(i);
                LocalDate fechaDesde = fechaInicial.plusMonths(i);
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

    public Integer predecirDemandaParaMesFuturo(DatosPrediccionDTO datosPrediccionDTO) throws Exception {
        switch (datosPrediccionDTO.getNombreMetodoPrediccion()) {
            case PROMEDIO_MOVIL_PONDERADO:
                return calculoPromedioMovilPonderado(datosPrediccionDTO);
            case PROMEDIO_MOVIL_SUAVIZADO: // Agrega casos para otros métodos de predicción
                return calculoPromedioMovilPonderadoSuavizado(datosPrediccionDTO);
            case REGRESION_LINEAL:
                return calcularRegresionLineal(datosPrediccionDTO);
            case ESTACIONALIDAD:
                return calcularEstacional(datosPrediccionDTO);
            default:
                throw new IllegalArgumentException("Método de predicción no soportado: " + datosPrediccionDTO.getNombreMetodoPrediccion());
        }
    }


    // PROMEDIO MOVIL PONDERADO
    //tener en cuenta q la ponderacion es el primero de la lista con el mes mas cercano
/*
    public Integer calculoPromedioMovilPonderado(DatosPrediccionDTO datosPrediccionDTO) throws Exception{
        try{
            // esto pq tienen q coincidir la cantidad de periodos con la cantidad de factores de ponderacion
            if(datosPrediccionDTO.getCantidadPeriodosHistoricos() != datosPrediccionDTO.getCoeficientesPonderacion().size()){
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
                    int anio = fechaDesde.getYear();
                    int mes = fechaDesde.getMonthValue();

                    PrediccionDemanda prediccionMesAnterior = prediccionDemandaRepository.findPrediccionArticuloByFecha(datosPrediccionDTO.getIdArticulo(), anio, mes);
                    if(prediccionMesAnterior != null && prediccionMesAnterior.getValorPrediccion() != null){
                        demandaHistorica = prediccionMesAnterior.getValorPrediccion();
                    } else{
                        demandaHistorica = 0;
                    }
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
    }*/



    //PARA PROMEDIO MOVIL PONDERADO SUAVIZADO EXPONENCIALMENTE
    //calculo promedio movil a usar en el suavizado --> usa 12 meses siempre

    public Integer calcularPromedioMovilMesAnterior(Long idArticulo, LocalDate fechaPrediccion) throws Exception{
        try {
            //System.out.println(" fecha prediccion mes anterior con pm: "+ fechaPrediccion);
            LocalDate fechaDesde = fechaPrediccion.minusMonths(12).withDayOfMonth(1);
            //System.out.println("la desde (pm): " + fechaDesde);
            LocalDate fechaHasta = fechaPrediccion.minusMonths(1).withDayOfMonth(fechaPrediccion.minusMonths(1).lengthOfMonth());
            //System.out.println("La hasta (pm): " + fechaHasta);
            int demandaHistorica = demandaHistoricaService.calcularDemandaHistorica(fechaDesde, fechaHasta, idArticulo);
            if(demandaHistorica <0){
                demandaHistorica = 0;
            }
            //System.out.println("la demanda historica para el PM es: "+ demandaHistorica);
            Integer valorPrediccion = demandaHistorica/12; //porque usa la anual pero ver si usamos otra
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
            //System.out.println("Fecha de prediccion a tomar: " + fechaPrediccion);
            LocalDate fechaDesde = fechaPrediccion.minusMonths(1).withDayOfMonth(1);
            //System.out.println("Fecha desde a tomar (pmps): " + fechaDesde);
            //LocalDate fechaHasta = fechaPrediccion.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            LocalDate fechaHasta = fechaPrediccion.minusMonths(1).withDayOfMonth(fechaPrediccion.minusMonths(1).lengthOfMonth());
            //System.out.println("Fecha hasta a tomar (pmps): " + fechaHasta);
            /*LocalDate fechaHasta = fechaPrediccion.minusMonths(1);

            if(fechaHasta.getMonthValue() == 2 && !fechaHasta.isLeapYear()){
                fechaHasta = fechaHasta.withDayOfMonth(28);
            } else {
                fechaHasta = fechaHasta.withDayOfMonth(fechaHasta.lengthOfMonth());
            }*/

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
                System.out.println("Numero Mes: " + nroMes);

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

                System.out.println("Demanda: " + demandaHistoricaMes);
                sumaXY += (nroMes * demandaHistoricaMes);

                sumaX2 += Math.pow(nroMes, 2);

                sumaDemandas += demandaHistoricaMes;
                sumaPeriodos += (nroMes);
            }
            System.out.println("suma x2:" + sumaX2);
            System.out.println("suma xy: " + sumaXY);
            Double promedioPeriodos = (double) (sumaPeriodos / datosPrediccionDTO.getCantidadPeriodosHistoricos()); // esto seria Tp o x con barrita
            System.out.println("suma de períodos (x):" + sumaPeriodos);
            System.out.println("promedio de periodos: " + promedioPeriodos);
            Double promedioDemandas =  (sumaDemandas / datosPrediccionDTO.getCantidadPeriodosHistoricos()); // esto seria Yp o y con barrita
            System.out.println("suma de demandas (y):" + sumaDemandas);
            System.out.println("promedio de demandas: " + promedioDemandas);
            double promPeriodosCuadrado = Math.pow(promedioPeriodos,2);

            b = (sumaXY - (datosPrediccionDTO.getCantidadPeriodosHistoricos() * promedioPeriodos * promedioDemandas)) / (sumaX2 - (datosPrediccionDTO.getCantidadPeriodosHistoricos() * promPeriodosCuadrado));
            System.out.println("VALOR DE b: " + b);
            a = promedioDemandas - (b * promedioPeriodos);
            System.out.println("VALOR DE a: " + a);

            Integer valorPrediccion = (int)(a + (b * (datosPrediccionDTO.getCantidadPeriodosHistoricos()+1)));

            return valorPrediccion;
        } catch(Exception e){
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
            // int anioAPredecir = LocalDate.now().getYear();
            //System.out.println("Año a predecir: " + datosPrediccionDTO.getAnioAPredecir());
            int anioActual = 0;
            int cantidadAnios = 3;
            Integer[][] demandaAnios = new Integer[cantidadAnios][cantidadMeses];
            Integer sumatoria = 0;
            for (int i = 0; i < cantidadAnios; i++) {
                anioActual = datosPrediccionDTO.getAnioAPredecir() - (i + 1);
                //System.out.println(anioActual);
                for (int j = 0; j < cantidadMeses; j++) {
                    //System.out.println(j);
                    int mes = j + 1;
                    LocalDate fechaDesde = LocalDate.of(anioActual, mes, 1);
                    LocalDate fechaHasta = LocalDate.of(anioActual, mes, fechaDesde.lengthOfMonth());
                    Integer demandaMesActual = demandaHistoricaService.calcularDemandaHistorica(fechaDesde, fechaHasta, datosPrediccionDTO.getIdArticulo());
                    //if (demandaMesActual < 0) {
                      //  demandaMesActual = 0;
                    //}
                    //System.out.println("La demanda del Mes: " + mes + " Año: " + anioActual + " fue: " + demandaMesActual);
                    demandaAnios[i][j] = demandaMesActual;
                    sumatoria = sumatoria + demandaMesActual;
                }
            }
            //System.out.println(Arrays.deepToString(demandaAnios));

            ///System.out.println("La sumatoria dio " + sumatoria);

            Double[] demandaAnualPromedio = new Double[cantidadMeses];
            Arrays.fill(demandaAnualPromedio, 0.0); // llena con 0
            Double demandaMensualPromedio = Double.valueOf((double) sumatoria / (cantidadAnios * cantidadMeses));
            //System.out.println("Demanda Mensual Promedio: " + demandaMensualPromedio);

            for (int t = 0; t < cantidadMeses; t++) {
                Integer sumaMes = 0;
                for (int k = 0; k < cantidadAnios; k++) {
                    sumaMes = sumaMes + demandaAnios[k][t];
                    //System.out.println("Llegué hasta acá, mes: " + t + ", año: " + k);
                }
                //System.out.println(sumaMes);
                demandaAnualPromedio[t] = Double.valueOf((double) sumaMes / cantidadAnios);
            }
            //System.out.println("demandaAnualPromedio:"+Arrays.toString(demandaAnualPromedio));
            Double[] indiceEstacionalMensual = new Double[cantidadMeses];
            for (int t = 0; t < cantidadMeses; t++) {
                //int mes = t + 1;
                indiceEstacionalMensual[t] = demandaAnualPromedio[t] / demandaMensualPromedio;
                prediccionDemandaMensual.set(t, (int) Math.ceil(indiceEstacionalMensual[t] * datosPrediccionDTO.getCantidadDemandaAnualTotal()/cantidadMeses));

                /* PrediccionDemanda prediccionDemanda = new PrediccionDemanda();
                prediccionDemanda.setArticulo(articuloService.findById(datosPrediccionDTO.getIdArticulo()));
                prediccionDemanda.setValorPrediccion(prediccionDemandaMensual.get(t));
                prediccionDemanda.setFechaPrediccion(LocalDate.of(datosPrediccionDTO.getAnioAPredecir(), mes, 1));
                prediccionDemanda.setNombreMetodoUsado(ESTACIONALIDAD);
                prediccionDemandaRepository.save(prediccionDemanda); */

                // System.out.println("Llegué a guardar");
            }
            //System.out.println("indiceEstacionalMensual: "+  Arrays.toString(indiceEstacionalMensual));
            //System.out.println("prediccionPorMes" + prediccionDemandaMensual);
            //System.out.println("LA PREDICCION ESSSSS: "+prediccionDemandaMensual.get(datosPrediccionDTO.getMesAPredecir()-1));
            return prediccionDemandaMensual.get(datosPrediccionDTO.getMesAPredecir()-1);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    //CREAR PREDICCION SEGUN EL METODO

}
