package invop.controllers;

import invop.dto.VentaDto;
import invop.services.ArticuloService;
import invop.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import invop.entities.Venta;
import invop.services.VentaServiceImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/ventas")
public class VentaController extends BaseControllerImpl<Venta, VentaServiceImpl>{

    @Autowired
    private ArticuloService articuloService;
    @Autowired
    private VentaService ventaService;


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

    @PostMapping("/crearVenta")
    @ResponseBody
    public ResponseEntity<Void> crearNuevaVenta(@RequestBody VentaDto ventaDTO) throws Exception {
        List<Long> articulosSinStockIds = articuloService.getArticulosSinStock(ventaDTO.getArticulosDetalleVenta());
        if (!articulosSinStockIds.isEmpty()) {
            StringBuilder mensajeError = new StringBuilder("Los siguientes artículos no tienen stock suficiente: ");
            for (Long id : articulosSinStockIds) {
                String nombreArticulo = articuloService.findById(id).getNombreArticulo();
                mensajeError.append(nombreArticulo).append(", ");
            }
            mensajeError.setLength(mensajeError.length() - 2); // eliminar última coma y espacio
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error-Message", mensajeError.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).build();
        }
        try {
            ventaService.crearVenta(ventaDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error-Message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // if (!articulosSinStock.isEmpty()) {
        // throw new RuntimeException(String.format("Hay articulos sin stock: %s", articulosSinStock));
        // ventaService.crearVenta(ventaDTO);
        // return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
