package invop.controllers;

import invop.entities.Proveedor;
import invop.services.ProveedorServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/proveedores")
public class ProveedorController extends BaseControllerImpl<Proveedor, ProveedorServiceImpl>{
}
