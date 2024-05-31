package invop.controllers;

import invop.entities.DemandaHistorica;
import invop.services.DemandaHistoricaImpl;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/demandashistoricas")

public class DemandaHistoricaController extends BaseControllerImpl<DemandaHistorica, DemandaHistoricaImpl> {
}
