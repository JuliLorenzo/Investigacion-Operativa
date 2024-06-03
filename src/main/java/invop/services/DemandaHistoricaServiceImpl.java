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
import java.util.Date;
import java.util.List;

@Service
public class DemandaHistoricaServiceImpl extends BaseServiceImpl<DemandaHistorica, Long> implements DemandaHistoricaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private DemandaHistoricaRepository demandaHistoricaRepository;
    public DemandaHistoricaServiceImpl(DemandaHistoricaRepository demandaHistoricaRepository){
        super(demandaHistoricaRepository);
        this.demandaHistoricaRepository = demandaHistoricaRepository;
    }

    public void calcularDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo){
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

}

