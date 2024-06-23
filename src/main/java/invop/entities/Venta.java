package invop.entities;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class Venta extends Base{

    @NotNull
    @Column(name = "fecha_venta")
    private LocalDate fechaVenta;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name="id_venta")
    @Builder.Default
    private List<VentaDetalle> ventaDetalles = new ArrayList<>();

    // @ManyToMany(mappedBy = "ventaList")
    // private List<DemandaHistorica> demandaHistoricaList;

    public void agregarDetalleVenta(VentaDetalle ventaDetalle){
        ventaDetalles.add(ventaDetalle);
    }

}
