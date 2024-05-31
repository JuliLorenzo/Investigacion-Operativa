package invop.controllers;

import invop.entities.OrdenCompraDetalle;
import invop.services.OrdenCompraDetalleServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/ordenescomprasdetalles")
public class OrdenCompraDetalleController extends BaseControllerImpl<OrdenCompraDetalle, OrdenCompraDetalleServiceImpl>{

}
