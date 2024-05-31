package invop.entities;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.Date;
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
    private Date fechaVenta;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name="id_venta")
    @Builder.Default
    private List<VentaDetalle> ventaDetalles = new ArrayList<>();


    public void agregarDetalleVenta(VentaDetalle ventaDetalle){
        ventaDetalles.add(ventaDetalle);
    }
}
