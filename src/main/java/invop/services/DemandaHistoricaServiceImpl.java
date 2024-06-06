package invop.services;

import invop.entities.DemandaHistorica;
import invop.repositories.ArticuloRepository;
import invop.repositories.DemandaHistoricaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DemandaHistoricaServiceImpl extends BaseServiceImpl<DemandaHistorica, Long> implements DemandaHistoricaService {

    @Autowired
    private DemandaHistoricaRepository demandaHistoricaRepository;

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private VentaService ventaService;


    public DemandaHistoricaServiceImpl(DemandaHistoricaRepository demandaHistoricaRepository, VentaService ventaService, ArticuloRepository articuloRepository){
        super(demandaHistoricaRepository);
        this.demandaHistoricaRepository = demandaHistoricaRepository;
        this.ventaService = ventaService;
        this.articuloRepository = articuloRepository;
    }

    public void crearDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo, LocalDateTime fechaAlta){
        int cantidadTotalFinal = calcularDemandaHistorica(fechaDesde,fechaHasta,idArticulo,fechaAlta);
        nuevaDemandaHistorica(fechaDesde,fechaHasta,idArticulo,cantidadTotalFinal,fechaAlta);
    }

    public Integer calcularDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo,LocalDateTime fechaAlta){
        int cantidadTotal = ventaService.calcularDemandaHistoricaArticulo(fechaDesde, fechaHasta, idArticulo,fechaAlta);
        return cantidadTotal;
    }

    public void nuevaDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo, Integer cantidadTotal, LocalDateTime fechaAlta){
        DemandaHistorica demandaHistorica = new DemandaHistorica();
        demandaHistorica.setFechaDesde(fechaDesde);
        demandaHistorica.setFechaHasta(fechaHasta);
        demandaHistorica.setFechaAlta(fechaAlta);
        demandaHistorica.setCantidadVendida(cantidadTotal);
        demandaHistorica.setArticulo(articuloRepository.findById(idArticulo).orElseThrow(() -> new EntityNotFoundException("Articulo no encontrado")));
        demandaHistoricaRepository.save(demandaHistorica);
    }
}

