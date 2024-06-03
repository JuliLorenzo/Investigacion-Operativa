package invop.services;

import invop.entities.DemandaHistorica;
import invop.repositories.DemandaHistoricaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;

@Service
public class DemandaHistoricaServiceImpl extends BaseServiceImpl<DemandaHistorica, Long> implements DemandaHistoricaService {

    @Autowired
    private DemandaHistoricaRepository demandaHistoricaRepository;

    /* @Autowired
   private VentaRepository ventaRepository;

    @Autowired
   private ArticuloRepository articuloRepository;

    */

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ArticuloService articuloService;

    public DemandaHistoricaServiceImpl(DemandaHistoricaRepository demandaHistoricaRepository, VentaService ventaService){
        super(demandaHistoricaRepository);
        this.demandaHistoricaRepository = demandaHistoricaRepository;
        this.ventaService = ventaService;
        this.articuloService = articuloService;
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
    public void calcularDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo) throws EntityNotFoundException {
        double cantidadTotalVendida = ventaService.calcularDemandaHistoricaArticulo(fechaDesde, fechaHasta, idArticulo);

        //crear y guardar la demanda historica
        DemandaHistorica demandaHistorica = new DemandaHistorica();
        demandaHistorica.setFechaDesde(fechaDesde);
        demandaHistorica.setFechaHasta(fechaHasta);
        demandaHistorica.setCantidadVendida(cantidadTotalVendida);
        //demandaHistorica.setArticulo(articuloRepository.findById(idArticulo).orElseThrow(() -> new EntityNotFoundException("Articulo no encontrado")));
        //demandaHistorica.setArticulo(articuloService.findById(idArticulo).orElseThrow(() -> new EntityNotFoundException("Articulo no encontrado")));
        demandaHistoricaRepository.save(demandaHistorica);

    }




}

