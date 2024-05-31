package invop.controllers;

import invop.entities.VentaDetalle;
import invop.services.VentaDetalleServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/ventasdetalles")
public class VentaDetalleController extends BaseControllerImpl<VentaDetalle, VentaDetalleServiceImpl>{

}
