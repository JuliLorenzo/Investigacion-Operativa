package invop.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="proveedor_articulo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProveedorArticulo extends Base {

    @Column(name = "tiempo_demora_articulo")
    private Double tiempoDemoraArticulo;

    @Column(name = "precio_articulo_proveedor")
    private Double precioArticuloProveedor;

    @ManyToOne
    @MapsId("articuloId")
    @JoinColumn(name = "articulo_id")
    private Articulo articulo;


}
