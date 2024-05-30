package invop.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "venta_detalles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class VentaDetalle extends Base{

    @NotNull
    @Column(name = "cantidad_vendida")
    private int cantidadVendida;

}
