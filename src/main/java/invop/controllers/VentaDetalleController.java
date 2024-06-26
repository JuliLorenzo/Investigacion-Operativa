package invop.controllers;

import invop.entities.VentaDetalle;
import invop.services.VentaDetalleServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/ventasdetalles")
public class VentaDetalleController extends BaseControllerImpl<VentaDetalle, VentaDetalleServiceImpl>{

    @GetMapping("/buscarDetallesPorVenta/{idVenta}")
    public ResponseEntity<?> buscarDetallesPorVenta(@PathVariable Long idVenta) {
         try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.buscarDetallesPorVenta(idVenta));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }

}
