package invop.controllers;

import invop.entities.Articulo;
import invop.services.ArticuloServiceImpl;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/articulos")

public class ArticuloController extends BaseControllerImpl<Articulo, ArticuloServiceImpl> {

}
