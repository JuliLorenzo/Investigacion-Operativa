package invop.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ProveedorArticuloId implements Serializable {

    @Column(name = "articulo_id")
    private int articuloId;

    @Column(name = "proveedor_id")
    private int proveedorId;
}
