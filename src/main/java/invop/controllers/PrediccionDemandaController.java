package invop.controllers;

import invop.dto.DatosPrediccionDTO;
import invop.entities.ErrorMetodo;
import invop.entities.PrediccionDemanda;
import invop.services.PrediccionDemandaServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/buscar/{idArticulo}")
    public ResponseEntity<List<PrediccionDemanda>> getPrediccionesSegunArticulo(@PathVariable Long idArticulo){
        try{
            List<PrediccionDemanda> listaPredicciones = servicio.buscarPrediccionesSegunArticulo(idArticulo);
            return ResponseEntity.ok(listaPredicciones);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/crearPredicciones")
    public ResponseEntity<List<PrediccionDemanda>> crearPredicciones(@RequestBody DatosPrediccionDTO datosPrediccionDTO) {
        try{
            List<PrediccionDemanda> listaPredicciones = servicio.crearPredicciones(datosPrediccionDTO);
            return ResponseEntity.ok(listaPredicciones);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
