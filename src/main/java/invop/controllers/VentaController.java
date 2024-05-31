package invop.controllers;

import invop.entities.Venta;
import invop.services.VentaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;


@Service
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/ventas")
public class VentaController extends BaseControllerImpl<Venta, VentaServiceImpl>{
}
