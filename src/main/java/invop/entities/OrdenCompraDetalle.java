package invop.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name="orden_compra_detalle")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrdenCompraDetalle extends Base{
    @Column(name="cantidad_a_comprar")
    private int cantidadAComprar;

}
