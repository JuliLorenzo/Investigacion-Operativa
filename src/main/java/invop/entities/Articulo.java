package invop.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "articulos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Articulo extends Base {

    @NotNull
    @Column(name = "nombre_articulo")
    private String nombreArticulo;

    @NotNull
    @Column(name = "cantidad_articulo")
    private int cantidadArticulo;

    @Column(name = "lote_optimo_articulo")
    private int loteOptimoArticulo;

    @Column(name = "punto_pedido_articulo")
    private int puntoPedidoArticulo;

    @Column(name = "stock_seguridad_articulo")
    private int stockSeguridadArticulo;

    @Column(name = "cgi_articulo")
    private Double cgiArticulo;

}
