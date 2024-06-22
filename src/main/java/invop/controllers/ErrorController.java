package invop.controllers;

import invop.dto.DatosPrediccionDTO;
import invop.entities.ErrorMetodo;
import invop.services.ErrorMetodoServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/errores")

public class ErrorController extends BaseControllerImpl<ErrorMetodo, ErrorMetodoServiceImpl>{

    @PostMapping("/calcularerror")
    public ResponseEntity<?> calcularErrorMetodo(@RequestBody DatosPrediccionDTO datosPrediccionDTO){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(servicio.crearErrorMetodo(datosPrediccionDTO));
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }
}
