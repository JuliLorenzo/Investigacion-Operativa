package invop.controllers;

import invop.dto.CrearArticuloDto;
import invop.entities.Articulo;
import invop.entities.OrdenCompra;
import invop.entities.OrdenCompraDetalle;
import invop.entities.ProveedorArticulo;
import invop.services.OrdenCompraService;
import invop.services.OrdenCompraServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/ordenescompras")
public class OrdenCompraController extends BaseControllerImpl<OrdenCompra, OrdenCompraServiceImpl> {

    @Autowired
    OrdenCompraService ordenCompraService;


    @GetMapping("/findOrdenesByEstado")
    public ResponseEntity<?> findOrdenesByEstado(@RequestParam String filtroEstado) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.findOrdenCompraByEstado(filtroEstado));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }

    @GetMapping("/articuloconordenactiva")
    public ResponseEntity<?> articuloconordenactiva(@RequestParam Long articuloId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.articuloConOrdenActiva(articuloId));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }

    @PostMapping("/crearAutomatica")
    public ResponseEntity<OrdenCompra> crearOrdenCompraAutomatica(@RequestBody Articulo articulo) throws Exception {
        OrdenCompra ordenCompra = ordenCompraService.crearOrdenCompraAutomatica(articulo);
        return ResponseEntity.ok(ordenCompra);
    }

    //Métodos para cambiar el estado de la orden de compra
    @PutMapping("/confirmar/{id}")
    public ResponseEntity<OrdenCompra> confirmarOrdenCompra(@PathVariable Long id) {
        OrdenCompra ordenCompra = ordenCompraService.confirmarOrdenCompra(id);
        return ResponseEntity.ok(ordenCompra);
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<OrdenCompra> cancelarOrdenCompra(@PathVariable Long id) {
        OrdenCompra ordenCompra = ordenCompraService.cancelarOrdenCompra(id);
        return ResponseEntity.ok(ordenCompra);
    }

    @PutMapping("/finalizar/{id}")
    public ResponseEntity<OrdenCompra> finalizarOrdenCompra(@PathVariable Long id) {
        OrdenCompra ordenCompra = ordenCompraService.finalizarOrdenCompra(id);
        return ResponseEntity.ok(ordenCompra);
    }

}
