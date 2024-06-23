package invop.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DemandaHistoricaDto {
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Long idArticulo;
}
