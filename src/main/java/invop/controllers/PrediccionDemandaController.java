package invop.controllers;

import invop.dto.DatosPrediccionDTO;
import invop.entities.PrediccionDemanda;
import invop.services.PrediccionDemandaServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/prediccionesdemandas")
public class PrediccionDemandaController extends BaseControllerImpl<PrediccionDemanda, PrediccionDemandaServiceImpl>{

    @PostMapping("/estacionalidad")
    public ResponseEntity<?> calcularEstacionalidad(@RequestBody DatosPrediccionDTO datosPrediccionDTO) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.calcularEstacional(datosPrediccionDTO));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }

    @PostMapping("/pmp")
    public ResponseEntity<?> calcularPromedioMovilPonderado(@RequestBody DatosPrediccionDTO datosPrediccionDTO) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.calculoPromedioMovilPonderado(datosPrediccionDTO));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }

    @PostMapping("/pmpsuav")
    public ResponseEntity<?> calcularPromedioMovilPonderadoSuavizado(@RequestBody DatosPrediccionDTO datosPrediccionDTO) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.calculoPromedioMovilPonderadoSuavizado(datosPrediccionDTO));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }

    @PostMapping("/rl")
    public ResponseEntity<?> calcularRegresionLineal(@RequestBody DatosPrediccionDTO datosPrediccionDTO) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.calcularRegresionLineal(datosPrediccionDTO));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }

}
