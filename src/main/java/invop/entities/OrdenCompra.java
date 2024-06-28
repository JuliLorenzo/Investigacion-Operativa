package invop.entities;

import invop.enums.EstadoOrdenCompra;
import invop.enums.ModeloInventario;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ordenes_compra")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class OrdenCompra extends Base{

    @Column(name = "fecha_orden_compra")
    private LocalDate fechaOrdenCompra;

    @Column(name = "estado_orden_compra")
    @Enumerated(EnumType.STRING)
    private EstadoOrdenCompra estadoOrdenCompra;

    @NotNull
    @ManyToOne()
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name="id_orden_compra")
    @Builder.Default
    private List<OrdenCompraDetalle> ordenCompraDetalles = new ArrayList<>();




}
