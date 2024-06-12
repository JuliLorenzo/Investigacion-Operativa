package invop.dto;

import lombok.Data;

@Data
public class ArticuloFaltanteDto {

    private Long idArticulo;

    private String nombreArticulo;

    private int stockActualArticulo;

    private int stockSeguridad;

    private boolean ordenActiva;

}
