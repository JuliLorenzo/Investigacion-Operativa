package invop.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class VentaDto {
    private LocalDate fechaHora;
    private Map<String, Integer> articulosDetalleVenta;
}
