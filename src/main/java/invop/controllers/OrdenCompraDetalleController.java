package invop.controllers;

import invop.entities.OrdenCompraDetalle;
import invop.services.OrdenCompraDetalleServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/ordenescomprasdetalles")
public class OrdenCompraDetalleController extends BaseControllerImpl<OrdenCompraDetalle, OrdenCompraDetalleServiceImpl>{

    @GetMapping("/buscarDetallesPorOC/{idOC}")
    public ResponseEntity<?> buscarDetallesPorOC(@PathVariable Long idOC) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.buscarDetallesPorOC(idOC));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }

}
