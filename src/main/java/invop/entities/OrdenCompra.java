package invop.entities;

import jakarta.persistence.*;
import lombok.*;

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
    private Date fechaOrdenCompra;

    @Column(name = "estado_orden_compra")
    private String estadoOrdenCompra;

    @Column(name = "total_orden_compra")
    private double totalOrdenCompra;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name="id_orden_compra")
    @Builder.Default
    private List<OrdenCompraDetalle> ordenCompraDetalles = new ArrayList<>();

    public void agregarDetalleOrdenCompra(OrdenCompraDetalle ordenCompraDetalle){
        ordenCompraDetalles.add(ordenCompraDetalle);
    }
}
