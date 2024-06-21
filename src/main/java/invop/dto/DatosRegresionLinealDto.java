package invop.dto;

import lombok.Data;

@Data
public class DatosRegresionLinealDto {
    private int cantPeriodosHistoricos;

    private int mesAPredecir;

    private int anioAPredecir;

    private Long idArticulo;

}
