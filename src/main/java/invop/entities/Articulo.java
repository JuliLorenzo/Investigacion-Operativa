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

    @Column(name = "lote_optimo_articulo")
    private Integer loteOptimoArticulo;

    //Revisar si no va en ProveedorArticulo
    @Column(name = "punto_pedido_articulo")
    private Integer puntoPedidoArticulo;

    //Revisar si no va en ProveedorArticulo
    @Column(name = "stock_seguridad_articulo")
    private Integer stockSeguridadArticulo;

    @Column(name = "cgi_articulo")
    private Double cgiArticulo;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name="id_articulo")
    @Builder.Default
    private List<OrdenCompra> ordenesCompra = new ArrayList<>();

    public void agregarOrdenCompra(OrdenCompra ordenCompra){
        ordenesCompra.add(ordenCompra);
    }

}
