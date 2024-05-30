package invop.entities;


import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "predicciones_demanda")
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
            name ="historicas-predicciones",
            joinColumns = @JoinColumn(name = "prediccion_demanda_id"),
            inverseJoinColumns = @JoinColumn(name = "demanda_historica_id")
    )
    private List <DemandaHistorica> demandaHistoricaList;

}
