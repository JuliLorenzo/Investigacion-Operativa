package invop.entities;

import jakarta.persistence.Entity;

import java.util.Date;

@Entity

public class OrdenCompra {

    private Date fechaOrdenCompra;
    private String estadoOrdenCompra;
    private float totalOrdenCompra;

}
