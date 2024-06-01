package invop.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import invop.entities.Venta;
import invop.services.VentaServiceImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/ventas")
public class VentaController extends BaseControllerImpl<Venta, VentaServiceImpl>{

    @GetMapping("/findVentasByFechas")
    public ResponseEntity<?> findVentasByFechas(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        try {
            LocalDate fechaDesde = desde;
            LocalDate fechaHasta = hasta;
            return ResponseEntity.status(HttpStatus.OK).body(servicio.findVentasByFechas(fechaDesde, fechaHasta));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }
}
