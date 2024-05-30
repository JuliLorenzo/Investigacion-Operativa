package invop.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Date;

@Entity
@Table(name = "articulo_insumo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Venta extends Base{


    @NotNull
    @Column(name = "fecha_venta")
    private Date fecha_venta;

}
