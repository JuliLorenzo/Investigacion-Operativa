package invop.services;

import invop.entities.PrediccionDemanda;
import invop.repositories.PrediccionDemandaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrediccionDemandaServiceImpl extends BaseServiceImpl<PrediccionDemanda, Long> implements PrediccionDemandaService {

    @Autowired
    private DemandaHistoricaService demandaHistoricaService;
    @Autowired
    private PrediccionDemandaRepository prediccionDemandaRepository;
    public PrediccionDemandaServiceImpl(PrediccionDemandaRepository prediccionDemandaRepository, DemandaHistoricaService demandaHistoricaService){
        super(prediccionDemandaRepository);
        this.prediccionDemandaRepository = prediccionDemandaRepository;
        this.demandaHistoricaService = demandaHistoricaService;
    }


    //promedio movil ponderado
    public Integer calculoPromedioMovilPonderado(int cantidadPeriodos, List<Double> coeficientesPonderacion, Long idArticulo, LocalDate fechaPrediccion) throws Exception{
        try{
            //esto pq tienen q coincidir la cantidad de periodos con el factor de ponderacion
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
}
