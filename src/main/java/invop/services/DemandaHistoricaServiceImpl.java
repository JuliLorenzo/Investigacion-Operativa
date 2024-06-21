package invop.services;

import invop.entities.DemandaHistorica;
import invop.entities.Venta;
import invop.entities.VentaDetalle;
import invop.repositories.ArticuloRepository;
import invop.repositories.DemandaHistoricaRepository;
import invop.repositories.VentaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DemandaHistoricaServiceImpl extends BaseServiceImpl<DemandaHistorica, Long> implements DemandaHistoricaService {

    @Autowired
    private DemandaHistoricaRepository demandaHistoricaRepository;

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private VentaRepository ventaRepository;


    public DemandaHistoricaServiceImpl(DemandaHistoricaRepository demandaHistoricaRepository, VentaRepository ventaRepository, ArticuloRepository articuloRepository){
        super(demandaHistoricaRepository);
        this.demandaHistoricaRepository = demandaHistoricaRepository;
        //this.ventaService = ventaService;
        this.articuloRepository = articuloRepository;
        this.ventaRepository = ventaRepository;
    }

    public void crearDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo, LocalDateTime fechaAlta){
        int cantidadTotalFinal = calcularDemandaHistorica(fechaDesde,fechaHasta,idArticulo);
        nuevaDemandaHistorica(fechaDesde,fechaHasta,idArticulo,cantidadTotalFinal,fechaAlta);
    }

    public Integer calcularDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo){
        int cantidadTotal = calcularDemandaHistoricaArticulo(fechaDesde, fechaHasta, idArticulo);
        return cantidadTotal;
    }

    public Integer calcularDemandaHistoricaArticulo(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo) {
        List<Venta> ventas = ventaRepository.findVentasByFechas(fechaDesde, fechaHasta);
        boolean existe = false;
        int cantidadTotalVendida = 0;
        //recorrer ventas y acumular la cantidad vendida del articulo
        for (Venta venta : ventas) {
            for (VentaDetalle detalle : venta.getVentaDetalles()) {
                if (detalle.getArticulo().getId().equals(idArticulo)) {
                    cantidadTotalVendida = cantidadTotalVendida + detalle.getCantidadVendida();
                    existe = true;
                }
            }
        }
        if (existe) {
            return cantidadTotalVendida;
        } else {
            return -1;
        }
    }

    public void nuevaDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo, Integer cantidadTotal, LocalDateTime fechaAlta){
        DemandaHistorica demandaHistorica = new DemandaHistorica();
        demandaHistorica.setFechaDesde(fechaDesde);
        demandaHistorica.setFechaHasta(fechaHasta);
        //demandaHistorica.setFechaAlta(fechaAlta);
        if(cantidadTotal < 0){
            cantidadTotal = 0;
        }
        demandaHistorica.setCantidadVendida(cantidadTotal);
        demandaHistorica.setArticulo(articuloRepository.findById(idArticulo).orElseThrow(() -> new EntityNotFoundException("Articulo no encontrado")));
        demandaHistoricaRepository.save(demandaHistorica);
    }
}

