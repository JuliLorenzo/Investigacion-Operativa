package invop.entities;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.NotFound;

@Entity
@Table(name = "errores")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ErrorMetodo extends Base {

    @NotNull
    @Column(name = "error")
    private Double PorcentajeError;


    @ManyToOne()
    @JoinColumn(name = "demanda_real")
    private DemandaHistorica demandaReal;


    @NotNull
    @ManyToOne()
    @JoinColumn(name = "id_prediccion_demanda")
    private PrediccionDemanda prediccionDemanda;

}
