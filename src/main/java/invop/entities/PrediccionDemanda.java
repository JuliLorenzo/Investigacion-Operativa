package invop.entities;


import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "articulo_id")
    private Articulo articulo;

}
