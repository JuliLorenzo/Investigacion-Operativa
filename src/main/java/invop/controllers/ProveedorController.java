package invop.controllers;

import invop.entities.Proveedor;
import invop.services.ProveedorService;
import invop.services.ProveedorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/proveedores")
public class ProveedorController extends BaseControllerImpl<Proveedor, ProveedorServiceImpl>{
    @Autowired
    private ProveedorServiceImpl proveedorServiceImpl;
}
