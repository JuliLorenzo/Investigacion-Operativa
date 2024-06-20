package invop.entities;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.NotFound;

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
    private Double PorcentajeError;


    @ManyToOne()
    @JoinColumn(name = "demanda_real")
    private DemandaHistorica demandaReal;


    @NotNull
    @ManyToOne()
    @JoinColumn(name = "id_prediccion_demanda")
    private PrediccionDemanda prediccionDemanda;

    @ManyToOne()
    @JoinColumn(name = "id_articulo")
    private Articulo articulo;

    @Column(name = "fecha_desde")
    private LocalDate fechaDesde;

    @Column(name = "fecha_hasta")
    private LocalDate fechaHasta;



}
