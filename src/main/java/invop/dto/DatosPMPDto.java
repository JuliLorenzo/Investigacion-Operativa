package invop.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class DatosPMPDto {
    private int cantidadPeriodos;

    private List<Double> coeficientesPonderacion;

    private Long idArticulo;

    //private LocalDate fechaPrediccion;

    private int mesAPredecir;

    private int anioAPredecir;

}
