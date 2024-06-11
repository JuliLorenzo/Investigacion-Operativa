package invop.entities;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proveedores")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class Proveedor extends Base {
    @NotNull
    @Column(name = "nombre_proveedor")
    private String nombreProveedor;

    /*@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name="id_proveedor")
    @Builder.Default
    private List<ProveedorArticulo> proveedorArticuloList = new ArrayList<>();
*/
}
