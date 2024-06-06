package invop.controllers;

import invop.entities.ProveedorArticulo;
import invop.services.ProveedorArticuloService;
import invop.services.ProveedorArticuloServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/proveedoresarticulos")
public class ProveedorArticuloController extends BaseControllerImpl<ProveedorArticulo, ProveedorArticuloServiceImpl>{

    @GetMapping("/findProveedoresByArticulo")
    public ResponseEntity<?> findProveedoresByArticulo(@RequestParam String filtroArticulo) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.findProveedoresByArticulo(filtroArticulo));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }

    @GetMapping("/findArticulosByProveedor")
    public ResponseEntity<?> findArticulosByProveedor(@RequestParam String filtroProveedor) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.findArticulosByProveedor(filtroProveedor));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }
}
