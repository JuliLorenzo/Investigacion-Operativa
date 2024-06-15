package invop.entities;

import invop.enums.ModeloInventario;
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

    @Column(name = "costo_almacenamiento")
    private Double costoAlmacenamientoArticulo;

    @Column(name = "costo_pedido")
    private Double costoPedidoArticulo;

    @Column(name = "stock_seguridad_articulo")
    private Integer stockSeguridadArticulo;

    @Column(name = "cgi_articulo")
    private Double cgiArticulo;

    @NotNull
    @Column(name = "modelo_inventario")
    @Enumerated(EnumType.STRING)
    private ModeloInventario modeloInventario;

    //Para el Metodo Lote Fijo
    @Column(name = "lote_optimo_articulo")
    private Integer loteOptimoArticulo;

    @Column(name = "punto_pedido_articulo")
    private Integer puntoPedidoArticulo;

    //Para el Metodo Intervalo Fijo
    @Column(name = "cantidad_maxima")
    private Integer cantidadMaximaArticulo;

    @Column(name = "tiempo_revision")
    private Integer tiempoRevisionArticulo;

    @Column(name = "demanda_anual")
    private Integer demandaAnualArticulo;

    @ManyToOne()
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedorPredeterminado;

    /*@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name="id_articulo")
    @Builder.Default
    private List<OrdenCompra> ordenesCompra = new ArrayList<>();


    public void agregarOrdenCompra(OrdenCompra ordenCompra){
        ordenesCompra.add(ordenCompra);
    }*/

}
