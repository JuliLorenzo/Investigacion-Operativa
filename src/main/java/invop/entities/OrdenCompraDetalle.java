package invop.entities;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name="orden_compra_detalles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class OrdenCompraDetalle extends Base {

    @Column(name="cantidad_a_comprar")
    private Integer cantidadAComprar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden_compra")
    private OrdenCompra ordenCompra;

    @NotNull
    @ManyToOne()
    @JoinColumn(name = "id_articulo")
    private Articulo articulo;

}
