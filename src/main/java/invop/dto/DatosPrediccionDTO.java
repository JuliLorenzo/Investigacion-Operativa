package invop.dto;

import invop.enums.NombreMetodoPrediccion;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DatosPrediccionDTO {
    private Integer cantidadPeriodosHistoricos;

    private Integer cantidadPeriodosAdelante;

    private List<Double> coeficientesPonderacion;

    private Long idArticulo;

    private Integer mesAPredecir;

    private Integer anioAPredecir;

    private Double alfa;

    private NombreMetodoPrediccion nombreMetodoPrediccion;

    private Integer cantidadDemandaAnualTotal;

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

}
