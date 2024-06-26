package invop.services;

import invop.entities.*;
import invop.enums.EstadoOrdenCompra;
import invop.repositories.OrdenCompraRepository;
import invop.repositories.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private ProveedorRepository proveedorRepository;

    public OrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository, ProveedorRepository proveedorRepository){
        super(ordenCompraRepository);
        this.ordenCompraRepository = ordenCompraRepository;
        this.proveedorRepository = proveedorRepository;

    }

    public List<OrdenCompra> findOrdenCompraByEstado(EstadoOrdenCompra estadoOrdenCompra) throws Exception {
        try {
            return ordenCompraRepository.findOrdenCompraByEstado(estadoOrdenCompra.name().toUpperCase());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<OrdenCompra> findOrdenCompraActiva() throws Exception {
        List<OrdenCompra> ordenesPendiente = findOrdenCompraByEstado(EstadoOrdenCompra.PENDIENTE);
        List<OrdenCompra> ordenesEnCurso = findOrdenCompraByEstado(EstadoOrdenCompra.EN_CURSO);

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
        } catch(Exception e) {
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

    public OrdenCompra modificarOCAutomatica(Long idOrdenCompra, Long idProveedor, Integer nuevaCantidad) throws Exception {
        try {
            Optional<OrdenCompra> ordenCompraOpcional = ordenCompraRepository.findById(idOrdenCompra);
            OrdenCompra ordenExiste = ordenCompraOpcional.orElseThrow(() -> new EntityNotFoundException("Orden de Compra no encontrada"));

            if (idProveedor != null) {
                Proveedor nuevoProveedor = proveedorRepository.findById(idProveedor)
                        .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado con el id: " + idProveedor));
                ordenExiste.setProveedor(nuevoProveedor);
            }

            if (nuevaCantidad != null && !ordenExiste.getOrdenCompraDetalles().isEmpty()) {
                OrdenCompraDetalle detalleExistente = ordenExiste.getOrdenCompraDetalles().get(0);
                detalleExistente.setCantidadAComprar(nuevaCantidad);
            }

            return ordenCompraRepository.save(ordenExiste);
        } catch (Exception e) {
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

