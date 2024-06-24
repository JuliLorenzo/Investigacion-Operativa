package invop.controllers;

import invop.dto.DatosPrediccionDTO;
import invop.entities.ErrorMetodo;
import invop.services.ErrorMetodoService;
import invop.services.ErrorMetodoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/errores")

public class ErrorController extends BaseControllerImpl<ErrorMetodo, ErrorMetodoServiceImpl>{

    @Autowired
    private ErrorMetodoService errorMetodoService;

    public ErrorController(ErrorMetodoService errorMetodoService) {
        this.errorMetodoService = errorMetodoService;
    }
/*
    @PostMapping("/calcularerror")
    public ResponseEntity<?> calcularErrorMetodo(@RequestBody DatosPrediccionDTO datosPrediccionDTO){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(servicio.crearErroresMetodos(datosPrediccionDTO));
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }*/

    @PostMapping("/crearErrores")
    public ResponseEntity<List<ErrorMetodo>> crearErroresMetodos(@RequestBody DatosPrediccionDTO datosPrediccionDTO){
        try{
            List<ErrorMetodo> listaErroresCreados = errorMetodoService.crearErroresMetodos(datosPrediccionDTO);
            return ResponseEntity.ok(listaErroresCreados);
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/buscar/{idArticulo}")
    public ResponseEntity<List<ErrorMetodo>> getErroresSegunArticulo(@PathVariable Long idArticulo){
        try{
            List<ErrorMetodo> listaErrores = errorMetodoService.buscarErroresSegunArticulo(idArticulo);
            return ResponseEntity.ok(listaErrores);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
