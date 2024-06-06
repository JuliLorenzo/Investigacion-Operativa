package invop.controllers;

import invop.entities.DemandaHistorica;
import invop.services.DemandaHistoricaService;
import invop.services.DemandaHistoricaServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/demandashistoricas")

public class DemandaHistoricaController extends BaseControllerImpl<DemandaHistorica, DemandaHistoricaServiceImpl> {

    @Autowired
    private DemandaHistoricaService demandaHistoricaService;

    @PostMapping("/calcularDemandaHistorica")
    public ResponseEntity<?> calcularDemandaHistorica(
            @RequestParam LocalDate desde, @RequestParam LocalDate hasta, @RequestParam Long idArticulo
    ) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            LocalDate fechaDesde = desde;
            LocalDate fechaHasta = hasta;
            LocalDateTime fechaAlta = LocalDateTime.now();
            demandaHistoricaService.crearDemandaHistorica(fechaDesde, fechaHasta, idArticulo, fechaAlta);
            return ResponseEntity.ok().build();
        } catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Articulo no encontrado");
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parametros inválidos");
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado");
        }
    }


}
