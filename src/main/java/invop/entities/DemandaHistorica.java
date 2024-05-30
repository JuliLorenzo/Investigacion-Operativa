package invop.entities;

import jakarta.persistence.*;

import java.util.Date;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;


@Entity
@Table(name = "demandas_historicas")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder



public class DemandaHistorica extends Base {

    @NotNull
    @Column(name = "fecha_desde")
    private Date fechaDesde;

    @NotNull
    @Column(name = "fecha_hasta")
    private Date fechaHasta;

    @NotNull
    @ManyToOne()
    @JoinColumn(name = "id_articulo")
    private Articulo articulo;

}
