package invop.entities;

import invop.enums.NombreMetodoPrediccion;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

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
    private Double porcentajeError;

    /*@ManyToOne()
    @JoinColumn(name = "demanda_real")
    private DemandaHistorica demandaReal;

    @NotNull
    @ManyToOne()
    @JoinColumn(name = "id_prediccion_demanda")
    private PrediccionDemanda prediccionDemanda;
*/
    @Column(name = "valor_demanda_real")
    private Integer valorDemandaReal;

    @Column(name = "valor_prediccion_demanda")
    private Integer valorPrediccionDemanda;

    @ManyToOne()
    @JoinColumn(name = "id_articulo")
    private Articulo articulo;

    @Column(name = "fecha_desde")
    private LocalDate fechaDesde;

    @Column(name = "fecha_hasta")
    private LocalDate fechaHasta;

    @Column(name = "nombre_metodo")
    private NombreMetodoPrediccion nombreMetodoUsado;



}
