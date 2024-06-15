package invop.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class VentaDto {
    //Puse que se ingrese la fecha de la venta para poder cargar ventas de meses/años anteriores
    private LocalDate fechaHora;
    private Map<String, Integer> articulosDetalleVenta;
}
