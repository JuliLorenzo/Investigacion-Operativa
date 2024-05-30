package invop.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "articulo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Articulo extends Base {

    @NotNull
    @Column(name = "nombre_articulo")
    private String nombre_articulo;

    @NotNull
    @Column(name = "precio_articulo")
    private Double precio_articulo;

    @NotNull
    @Column(name = "cantidad_articulo")
    private int cantidad_articulo;

    @Column(name = "lote_optimo_articulo")
    private int lote_optimo_articulo;

    @Column(name = "punto_pedido_articulo")
    private int punto_pedido_articulo;

    @Column(name = "stock_seguridad_articulo")
    private int stock_seguridad_articulo;

    @Column(name = "cgi_articulo")
    private Double cgi_articulo;

}
