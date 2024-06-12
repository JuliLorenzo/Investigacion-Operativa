package invop.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "tabla_valores_z")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TablaValoresZ extends Base{

    @NotNull
    @Column(name = "porcentaje")
    private Double porcentajeNivelSignif;

    @NotNull
    @Column(name = "z_critico")
    private Double zCritico;

}
