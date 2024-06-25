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

    //@NotNull
    @Column(name = "cantidad_vendida")
    private Integer cantidadVendida;

    @NotNull
    @ManyToOne()
    @JoinColumn(name = "id_articulo")
    private Articulo articulo;

    //@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    //@JoinTable(
    //        name ="historicas-ventas",
    //        joinColumns = @JoinColumn(name = "demanda_historica_id"),
    //        inverseJoinColumns = @JoinColumn(name = "venta_id")
    //)
    //private List <Venta> ventaList;

}
