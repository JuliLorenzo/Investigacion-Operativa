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

    public Integer obtenerDemandaHistoricaOProyectada(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo) {
        int demandaHistorica = calcularDemandaHistoricaArticulo(fechaDesde, fechaHasta, idArticulo);

        if (demandaHistorica == 0) {
            LocalDate hoy = LocalDate.now();
            if (fechaHasta.isBefore(hoy)) {
                // Si la fechaHasta es anterior al día actual, usar 0 como demanda histórica --> no hubieron ventas de ese articulo
                return 0;
            } else {
                // Si el mes no ha pasado o no ha terminado, avisar que hay que hacer la predicción para ese mes --> no existe todavia la demanda historica, el mes todavia no pasa
                return -1;
            }
        }

        return demandaHistorica;
    }

    public DemandaHistorica crearDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo){
        int cantidadTotalFinal = calcularDemandaHistorica(fechaDesde,fechaHasta,idArticulo);
        return nuevaDemandaHistorica(fechaDesde,fechaHasta,idArticulo,cantidadTotalFinal);

    }

    public Integer calcularDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo){
        //System.out.println("La fecha desde es: "+ fechaDesde + " la fecha hasta es: "+ fechaHasta);
        int cantidadTotal = calcularDemandaHistoricaArticulo(fechaDesde, fechaHasta, idArticulo);
        return cantidadTotal;
    }

    public Integer calcularDemandaHistoricaArticulo(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo) {
        List<Venta> ventas = ventaRepository.findVentasByFechas(fechaDesde, fechaHasta);
        int cantidadTotalVendida = 0;

        //recorrer ventas y acumular la cantidad vendida del articulo
        for (Venta venta : ventas) {
            for (VentaDetalle detalle : venta.getVentaDetalles()) {
                if (detalle.getArticulo().getId().equals(idArticulo)) {
                    cantidadTotalVendida += detalle.getCantidadVendida();
                }
            }
        }
            return cantidadTotalVendida;
    }

    public DemandaHistorica nuevaDemandaHistorica(LocalDate fechaDesde, LocalDate fechaHasta, Long idArticulo, Integer cantidadTotal){
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
        return demandaHistorica;

    }
}

