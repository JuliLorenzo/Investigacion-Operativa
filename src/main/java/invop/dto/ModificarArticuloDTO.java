package invop.dto;


import invop.enums.ModeloInventario;
import lombok.Data;

@Data
public class ModificarArticuloDTO {
    private Long idArticulo;
    private String nombreArticulo;
    private ModeloInventario modeloInventario;
    private Long proveedorPredeterminadoId;
    private Double tiempoRevisionArticulo;
}
