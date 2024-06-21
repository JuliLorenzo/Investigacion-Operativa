package invop.controllers;

import invop.dto.DatosPMPDto;
import invop.entities.PrediccionDemanda;
import invop.services.PrediccionDemandaServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/prediccionesdemandas")
public class PrediccionDemandaController extends BaseControllerImpl<PrediccionDemanda, PrediccionDemandaServiceImpl>{

    @PostMapping("/estacionalidad")
    public ResponseEntity<?> calcularEstacionalidad(@RequestParam Long idArticulo, @RequestParam Integer cantidadDemandaAnualTotal, @RequestParam Integer anioAPredecir) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.calcularEstacional(idArticulo, cantidadDemandaAnualTotal, anioAPredecir));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }

    @PostMapping("/pmp")
    public ResponseEntity<?> calcularPromedioMovilPonderado(@RequestBody DatosPMPDto datosPMP) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.calculoPromedioMovilPonderado(datosPMP));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }

}
