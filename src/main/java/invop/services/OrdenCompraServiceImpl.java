package invop.services;

import invop.entities.OrdenCompra;
import invop.entities.OrdenCompraDetalle;
import invop.repositories.OrdenCompraRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

import java.util.List;
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

}
