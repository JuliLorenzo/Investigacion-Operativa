package invop.controllers;


import invop.entities.TablaValoresZ;
import invop.services.TablaValoresZService;
import invop.services.TablaValoresZServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/tablavaloresz")
public class TablaValoresZController extends BaseControllerImpl<TablaValoresZ, TablaValoresZServiceImpl>{
    @Autowired
    private TablaValoresZService tablaValoresZService;
}
