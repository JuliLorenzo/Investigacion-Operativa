package invop.services;

import invop.entities.*;
import invop.enums.EstadoOrdenCompra;
import invop.repositories.OrdenCompraRepository;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class OrdenCompraServiceImpl extends BaseServiceImpl<OrdenCompra, Long> implements OrdenCompraService {

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;


    public OrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository, OrdenCompraDetalleService ordenCompraDetalleService){
        super(ordenCompraRepository);
        this.ordenCompraRepository = ordenCompraRepository;

    }

    @Override
    @Transactional
    public List<OrdenCompra> findOrdenCompraByEstado(String filtroEstado) throws Exception{
        try {
            List<OrdenCompra> buscarOrdenes = ordenCompraRepository.findOrdenCompraByEstado(filtroEstado);
            return buscarOrdenes;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public List<OrdenCompra> findOrdenCompraActiva() throws Exception {
        List<OrdenCompra> ordenesPendiente = findOrdenCompraByEstado("Pendiente");
        List<OrdenCompra> ordenesEnCurso = findOrdenCompraByEstado("En Curso");

        List<OrdenCompra> ordenesActivas = new ArrayList<>(ordenesPendiente);
        ordenesActivas.addAll(ordenesEnCurso);
        return ordenesActivas;
    }

    public boolean articuloConOrdenActiva(Long articuloId) throws Exception{
        try {
            List<OrdenCompra> ordenesActivas = findOrdenCompraActiva();

            //Recorrer las ordenes de compra activas
            for (OrdenCompra orden : ordenesActivas) {
                //Recorre los detalles de las ordenes de compras
                for (OrdenCompraDetalle detalle : orden.getOrdenCompraDetalles()) {
                    //Verificar si el detalle contiene el articulo ingresado
                    if (detalle.getArticulo().getId().equals(articuloId)) {
                        return true;
                    }
                }
            }
        } catch(Exception e){
            throw new Exception(e.getMessage());
            }
        return false;

        }

    //CREAR ORDEN DE COMPRA AUTOMATICA

    @Override
    @Transactional
    public OrdenCompra crearOrdenCompraAutomatica(Articulo articulo) throws Exception{
        try {
            OrdenCompra ordenAutomatica = new OrdenCompra();

            ordenAutomatica.setFechaOrdenCompra(LocalDate.now());
            ordenAutomatica.setEstadoOrdenCompra(EstadoOrdenCompra.PENDIENTE);
            ordenAutomatica.setProveedor(articulo.getProveedorPredeterminado());

            OrdenCompraDetalle detalle = new OrdenCompraDetalle();
            detalle.setArticulo(articulo);
            detalle.setCantidadAComprar(articulo.getLoteOptimoArticulo());

            List<OrdenCompraDetalle> listaDetalle = new ArrayList<>();
            listaDetalle.add(detalle);

            ordenAutomatica.setOrdenCompraDetalles(listaDetalle);

            ordenCompraRepository.save(ordenAutomatica);
            return ordenAutomatica;
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }
    public OrdenCompra confirmarOrdenCompra(Long ordenCompraId) {
        Optional<OrdenCompra> optionalOrdenCompra = ordenCompraRepository.findById(ordenCompraId);
        if (optionalOrdenCompra.isPresent()) {
            OrdenCompra ordenCompra = optionalOrdenCompra.get();
            ordenCompra.setEstadoOrdenCompra(EstadoOrdenCompra.EN_CURSO);
            ordenCompraRepository.save(ordenCompra);
            return ordenCompra;
        } else {
            throw new RuntimeException("Orden de compra no encontrada");
        }
    }

    public OrdenCompra cancelarOrdenCompra(Long ordenCompraId) {
        Optional<OrdenCompra> optionalOrdenCompra = ordenCompraRepository.findById(ordenCompraId);
        if (optionalOrdenCompra.isPresent()) {
            OrdenCompra ordenCompra = optionalOrdenCompra.get();
            ordenCompra.setEstadoOrdenCompra(EstadoOrdenCompra.CANCELADA);
            ordenCompraRepository.save(ordenCompra);
            return ordenCompra;
        } else {
            throw new RuntimeException("Orden de compra no encontrada");
        }
    }
    public OrdenCompra finalizarOrdenCompra(Long ordenCompraId) {
        Optional<OrdenCompra> optionalOrdenCompra = ordenCompraRepository.findById(ordenCompraId);
        if (optionalOrdenCompra.isPresent()) {
            OrdenCompra ordenCompra = optionalOrdenCompra.get();
            ordenCompra.setEstadoOrdenCompra(EstadoOrdenCompra.FINALIZADA);
            ordenCompraRepository.save(ordenCompra);
            return ordenCompra;
        } else {
            throw new RuntimeException("Orden de compra no encontrada");
        }
    }

}
