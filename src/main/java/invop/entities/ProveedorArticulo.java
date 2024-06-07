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

    @Column(name = "costo_pedido")
    private Double costoPedidoArticuloProveedor;

    @Column(name = "costo_almacenamiento")
    private Double costoAlmacenamientoArticuloProveedor;

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

    @ManyToOne
    @JoinColumn(name = "articulo_id")
    private Articulo articulo;


}
