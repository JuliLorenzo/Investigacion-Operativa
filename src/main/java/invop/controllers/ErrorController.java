package invop.controllers;

import invop.entities.ErrorMetodo;
import invop.services.ErrorMetodoServiceImpl;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/errores")

public class ErrorController extends BaseControllerImpl<ErrorMetodo, ErrorMetodoServiceImpl>{
}
