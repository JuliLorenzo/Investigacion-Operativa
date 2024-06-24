package invop.controllers;

import invop.dto.CrearArticuloDto;
import invop.entities.Articulo;
import invop.entities.OrdenCompra;
import invop.entities.OrdenCompraDetalle;
import invop.entities.ProveedorArticulo;
import invop.enums.EstadoOrdenCompra;
import invop.services.ArticuloService;
import invop.services.OrdenCompraService;
import invop.services.OrdenCompraServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/ordenescompras")
public class OrdenCompraController extends BaseControllerImpl<OrdenCompra, OrdenCompraServiceImpl> {

    @Autowired
    private OrdenCompraService ordenCompraService;
    @Autowired
    private ArticuloService articuloService;

    @GetMapping("/findOrdenesByEstado")
    public ResponseEntity<?> findOrdenesByEstado(@RequestParam EstadoOrdenCompra filtroEstado) {
        try {
            List<OrdenCompra> ordenes = ordenCompraService.findOrdenCompraByEstado(filtroEstado);
            return ResponseEntity.status(HttpStatus.OK).body(ordenes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/articuloconordenactiva")
    public ResponseEntity<?> articuloconordenactiva(@RequestParam Long articuloId) {
        try {
            boolean exists = servicio.articuloConOrdenActiva(articuloId);
            return ResponseEntity.status(HttpStatus.OK).body(exists);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/crearAutomatica")
    public ResponseEntity<OrdenCompra> crearOrdenCompraAutomatica(@RequestBody Articulo articulo) throws Exception {
        OrdenCompra ordenCompra = ordenCompraService.crearOrdenCompraAutomatica(articulo);
        return ResponseEntity.ok(ordenCompra);
    }

    //Métodos para cambiar el estado de la orden de compra
    //PENDIENTE --> EN CURSO
    @PutMapping("/confirmar/{id}")
    public ResponseEntity<OrdenCompra> confirmarOrdenCompra(@PathVariable Long id) {
        OrdenCompra ordenCompra = ordenCompraService.confirmarOrdenCompra(id);
        return ResponseEntity.ok(ordenCompra);
    }

    // EN CURSO - PENDIENDE  --> CANCELADA
    @PutMapping("/cancelar/{id}")
    public ResponseEntity<OrdenCompra> cancelarOrdenCompra(@PathVariable Long id) {
        OrdenCompra ordenCompra = ordenCompraService.cancelarOrdenCompra(id);
        return ResponseEntity.ok(ordenCompra);
    }


    //EN CURSO --> FINALIZADA
    @PutMapping("/finalizar/{id}")
    public ResponseEntity<OrdenCompra> finalizarOrdenCompra(@PathVariable Long id) {
        OrdenCompra ordenCompra = ordenCompraService.finalizarOrdenCompra(id);

        OrdenCompraDetalle detalle = ordenCompra.getOrdenCompraDetalles().get(0);
        Articulo articulo = detalle.getArticulo();
        Integer cantidadPedida = detalle.getCantidadAComprar();
        articuloService.aumentarStock(articulo, cantidadPedida);

        return ResponseEntity.ok(ordenCompra);
    }

}
