package invop.services;

import invop.entities.DemandaHistorica;
import invop.repositories.ArticuloRepository;
import invop.repositories.DemandaHistoricaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;

@Service
public class DemandaHistoricaServiceImpl extends BaseServiceImpl<DemandaHistorica, Long> implements DemandaHistoricaService {

    @Autowired
    private DemandaHistoricaRepository demandaHistoricaRepository;

    /* @Autowired
   private VentaRepository ventaRepository;
   */

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

    /* public void calcularDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo){
        List<Venta> ventas = ventaRepository.findVentasByFechas(fechaDesde, fechaHasta);

        double cantidadTotalVendida = 0;
        //recorrer ventas y acumular la cantidad vendida del articulo
        for(Venta venta : ventas){
            for(VentaDetalle detalle : venta.getVentaDetalles()){
                if (detalle.getArticulo().getId().equals(idArticulo)){
                    cantidadTotalVendida = cantidadTotalVendida + detalle.getCantidadVendida();
                }
            }
        }
        //crear y guardar la demanda historica
        DemandaHistorica demandaHistorica = new DemandaHistorica();
        demandaHistorica.setFechaDesde(fechaDesde);
        demandaHistorica.setFechaHasta(fechaHasta);
        demandaHistorica.setCantidadVendida(cantidadTotalVendida);
        demandaHistorica.setArticulo(articuloRepository.findById(idArticulo).orElseThrow(() -> new EntityNotFoundException("Articulo no encontrado")));

        demandaHistoricaRepository.save(demandaHistorica);
    }
    */

    @Transactional
    public void calculoNuevaDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo){
        double cantidadTotal = ventaService.calcularDemandaHistoricaArticulo(fechaDesde, fechaHasta, idArticulo);
        nuevaDemandaHistorica(fechaDesde, fechaHasta, idArticulo, cantidadTotal);
    }
    @Override
    public double calcularDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo){
        double cantidadTotalVendida = ventaService.calcularDemandaHistoricaArticulo(fechaDesde, fechaHasta, idArticulo);
        return cantidadTotalVendida;
    }

    @Transactional
    public void nuevaDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo, Double cantidadTotalVendida){
        DemandaHistorica demandaHistorica = new DemandaHistorica();
        demandaHistorica.setFechaDesde(fechaDesde);
        demandaHistorica.setFechaHasta(fechaHasta);
        demandaHistorica.setCantidadVendida(cantidadTotalVendida);
        demandaHistorica.setArticulo(articuloRepository.findById(idArticulo).orElseThrow(() -> new EntityNotFoundException("Articulo no encontrado")));
        demandaHistoricaRepository.save(demandaHistorica);
    }



}

