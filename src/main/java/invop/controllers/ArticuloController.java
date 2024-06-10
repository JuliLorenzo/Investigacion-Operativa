package invop.controllers;

import invop.entities.Articulo;
import invop.enums.ModeloInventario;
import invop.services.ArticuloService;
import invop.services.ArticuloServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/articulos")

public class ArticuloController extends BaseControllerImpl<Articulo, ArticuloServiceImpl> {

    @Autowired
    private ArticuloService articuloService;

    @Autowired
    public ArticuloController(ArticuloService articuloService) {
        this.articuloService = articuloService;
    }

    //Revisar si queremos devolver un String en vez de un boolean (y cambiar las exception)
    @GetMapping("/{id}/control-orden-compra")
    public ResponseEntity<Boolean> controlOrdenCompraActiva(@PathVariable Long id) {
        try {
            boolean tieneOrdenActiva = articuloService.controlOrdenCompraActiva(id);
            return ResponseEntity.ok(tieneOrdenActiva);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> crearArticulo(@RequestParam Long idProveedor, @RequestParam ModeloInventario modeloInventario, @RequestBody Articulo articulo) {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(articuloService.crearArticulo(idProveedor, articulo.getNombreArticulo(), modeloInventario, articulo.getCantidadArticulo()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Error, por favor intente más tarde\"}");
        }
    }

}
