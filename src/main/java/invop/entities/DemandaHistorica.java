package invop.entities;

import jakarta.persistence.*;
import java.time.*;
import java.util.List;
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
    private LocalDate fechaDesde;

    @NotNull
    @Column(name = "fecha_hasta")
    private LocalDate fechaHasta;

    @Column(name = "cantidad_vendida")
    private Integer cantidadVendida;

    @NotNull
    @ManyToOne()
    @JoinColumn(name = "id_articulo")
    private Articulo articulo;


}
