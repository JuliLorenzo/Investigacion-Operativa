package invop.controllers;

import invop.dto.ArticuloAReponerDto;
import invop.dto.ArticuloFaltanteDto;
import invop.dto.CrearArticuloDto;
import invop.dto.ModificarArticuloDTO;
import invop.entities.Articulo;
import invop.entities.Proveedor;
import invop.entities.ProveedorArticulo;
import invop.enums.ModeloInventario;
import invop.services.ArticuloService;
import invop.services.ArticuloServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;


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

    @GetMapping("/faltantes")
    public ResponseEntity<List<ArticuloFaltanteDto>> getArticulosFaltantes() {
        try {
            List<Articulo> articulosFaltantes = articuloService.listadoFaltantes();
            List<ArticuloFaltanteDto> faltantesFinal = new ArrayList<>();
            for(Articulo articulo : articulosFaltantes){
                ArticuloFaltanteDto faltante = new ArticuloFaltanteDto();
                faltante.setIdArticulo(articulo.getId());
                faltante.setNombreArticulo(articulo.getNombreArticulo());
                faltante.setStockActualArticulo(articulo.getCantidadArticulo());
                faltante.setStockSeguridad(articulo.getStockSeguridadArticulo());
                faltante.setOrdenActiva(articuloService.controlOrdenCompraActiva(articulo.getId()));
                faltantesFinal.add(faltante);
            }
            return ResponseEntity.ok(faltantesFinal);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/reponer")
    public ResponseEntity<List<ArticuloAReponerDto>> getArticulosAReponer() {
        try {
            List<Articulo> articulosAReponer = articuloService.listadoAReponer();
            List<ArticuloAReponerDto> ReponerFinal = new ArrayList<>();
            for (Articulo articulo : articulosAReponer) {
                ArticuloAReponerDto areponer = new ArticuloAReponerDto();
                areponer.setIdArticulo(articulo.getId());
                areponer.setNombreArticulo(articulo.getNombreArticulo());
                areponer.setPuntoPedido(articulo.getPuntoPedidoArticulo());
                areponer.setStockSeguridad(articulo.getStockSeguridadArticulo());
                areponer.setCantidadArticulo(articulo.getCantidadArticulo());
                areponer.setOrdenActiva(articuloService.controlOrdenCompraActiva(articulo.getId()));
                ReponerFinal.add(areponer);
            }
            return ResponseEntity.ok(ReponerFinal);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PatchMapping("/modificar/{idArticulo}")
    public ResponseEntity<?> modificarArticulo(@RequestBody ModificarArticuloDTO articuloModificado){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(articuloService.modificarArticulo(articuloModificado));
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Error, por favor intente más tarde\"}");
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearArticulo(@RequestBody CrearArticuloDto crearArticuloDto){
        try {
            Articulo articulo = crearArticuloDto.getArticulo();
            ProveedorArticulo proveedorArticulo = crearArticuloDto.getProveedorArticulo();
            return ResponseEntity.status(HttpStatus.OK).body(articuloService.crearArticulo(articulo, proveedorArticulo));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Error, por favor intente más tarde\"}");
        }
    }

    @GetMapping("/{articuloId}/proveedor-predeterminado")
    public ResponseEntity<Proveedor> obtenerProveedorPredeterminado(@PathVariable Long articuloId) {
        Proveedor proveedor = articuloService.obtenerProveedorPredeterminado(articuloId);
        return ResponseEntity.ok(proveedor);
    }

    @DeleteMapping("/borrarArticulo/{id}")
    public ResponseEntity<?> borrarArticulo(@PathVariable Long id){
        try{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(articuloService.darDeBajaArticulo(id));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Error, por favor intente más tarde\"}");
        }
    }

}
