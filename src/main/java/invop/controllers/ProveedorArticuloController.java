package invop.controllers;

import invop.entities.ProveedorArticulo;
import invop.services.ProveedorArticuloServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/proveedoresarticulos")
public class ProveedorArticuloController extends BaseControllerImpl<ProveedorArticulo, ProveedorArticuloServiceImpl>{
}
