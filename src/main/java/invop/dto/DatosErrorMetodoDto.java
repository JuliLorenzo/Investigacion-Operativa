package invop.dto;

import invop.enums.NombreMetodoPrediccion;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DatosErrorMetodoDto {
    private NombreMetodoPrediccion nombreMetodoPrediccion;

    private Long idArticulo;

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
}
