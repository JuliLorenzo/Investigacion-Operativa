package invop.dto;

import lombok.Data;

@Data
public class ArticuloAReponerDto {
    private Long idArticulo;

    private String nombreArticulo;

    private int puntoPedido;

    private int stockSeguridad;

    private boolean ordenActiva;
}
