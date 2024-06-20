package invop.dto;

import invop.entities.Articulo;
import invop.entities.ProveedorArticulo;
import lombok.Data;

@Data
public class CrearArticuloDto {
    private Articulo articulo;
    private ProveedorArticulo proveedorArticulo;

}
