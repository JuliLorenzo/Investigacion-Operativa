package invop.controllers;

import invop.dto.CrearArticuloDto;
import invop.entities.Articulo;
import invop.entities.OrdenCompra;
import invop.entities.OrdenCompraDetalle;
import invop.entities.ProveedorArticulo;
import invop.services.OrdenCompraServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/ordenescompras")
public class OrdenCompraController extends BaseControllerImpl<OrdenCompra, OrdenCompraServiceImpl> {


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
            boolean exists = servicio.articuloConOrdenActiva(articuloId);
            return ResponseEntity.status(HttpStatus.OK).body(exists);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

}
