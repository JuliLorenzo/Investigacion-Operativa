package invop.entities;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

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
    private int cantidadArticulo;

    @Column(name = "lote_optimo_articulo")
    private int loteOptimoArticulo;

    @Column(name = "punto_pedido_articulo")
    private int puntoPedidoArticulo;

    @Column(name = "stock_seguridad_articulo")
    private int stockSeguridadArticulo;

    @Column(name = "cgi_articulo")
    private Double cgiArticulo;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name="id_articulo")
    @Builder.Default
    private List<OrdenCompra> ordenesCompra = new ArrayList<>();

    public void agregarOrdenCompra(OrdenCompra ordenCompra){
        ordenesCompra.add(ordenCompra);
    }

}
