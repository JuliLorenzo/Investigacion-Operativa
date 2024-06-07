package invop.entities;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "articulos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Articulo extends Base {

    @NotNull
    @Column(name = "nombre_articulo")
    private String nombreArticulo;

    @NotNull
    @Column(name = "cantidad_articulo")
    private Integer cantidadArticulo;

    @ManyToOne()
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedorPredeterminado;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name="id_articulo")
    @Builder.Default
    private List<OrdenCompra> ordenesCompra = new ArrayList<>();

    public void agregarOrdenCompra(OrdenCompra ordenCompra){
        ordenesCompra.add(ordenCompra);
    }

}
