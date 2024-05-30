package invop.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "predicciones-demanda")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PrediccionDemanda extends Base{

    @NotNull
    @Column(name = "valor_prediccion")
    private int valorPrediccion;

}
