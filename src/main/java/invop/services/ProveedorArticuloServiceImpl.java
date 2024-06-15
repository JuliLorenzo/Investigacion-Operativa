package invop.services;

import invop.entities.ProveedorArticulo;
import invop.repositories.ProveedorArticuloRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorArticuloServiceImpl extends BaseServiceImpl<ProveedorArticulo, Long> implements ProveedorArticuloService {

    @Autowired
    private ProveedorArticuloRepository proveedorArticuloRepository;

    public ProveedorArticuloServiceImpl(ProveedorArticuloRepository proveedorArticuloRepository){
        super(proveedorArticuloRepository);
        this.proveedorArticuloRepository = proveedorArticuloRepository;
    }

    @Override
    @Transactional
    public List<ProveedorArticulo> findProveedoresByArticulo(Long filtroArticulo) throws Exception{
        try {
            return proveedorArticuloRepository.findProveedoresByArticulo(filtroArticulo);
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<ProveedorArticulo> findArticulosByProveedor(Long filtroProveedor) throws Exception {
        try {
            List<ProveedorArticulo> buscarArticulos = proveedorArticuloRepository.findArticulosByProveedor(filtroProveedor);
            return buscarArticulos;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Double findCostoPedidoByArticuloAndProveedor(Long idArticulo, Long idProveedor) throws Exception{
        try {
            Double valorCP = proveedorArticuloRepository.findCostoPedidoByArticuloAndProveedor(idArticulo, idProveedor);
            return valorCP;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Double findTiempoDemoraArticuloByArticuloAndProveedor(Long idArticulo, Long idProveedor) throws Exception{
        try {
            Double tiempoDemora = proveedorArticuloRepository.findTiempoDemoraArticuloByArticuloAndProveedor(idArticulo, idProveedor);
            return tiempoDemora;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    //POSIBLE ELIMINACION -> REEMPLAZO CON UNA @QUERY
    //Busca el Costo de Pedido de un ProveedorArticulo.
    public ProveedorArticulo findArticuloDeProveedorDeterminado(List<ProveedorArticulo> articulosDelProveedor, Long idArticulo) throws Exception {
        try {
            for (ProveedorArticulo proveedorArticulo : articulosDelProveedor){
                if (proveedorArticulo.getArticulo().getId().equals(idArticulo)){
                    ProveedorArticulo articuloEncontrado = proveedorArticulo;
                    return articuloEncontrado;
                }
            }
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
        return null;
    }

    @Transactional
    public Double findCostoPedido(Long idArticulo, Long idProveedor) throws Exception {
        try {
            List<ProveedorArticulo> articulos = proveedorArticuloRepository.findArticulosByProveedor(idProveedor);
            ProveedorArticulo articuloProveedor = findArticuloDeProveedorDeterminado(articulos, idArticulo);
            if (articuloProveedor == null) {
                throw new Exception("Artículo no encontrado para el proveedor especificado.");
            }
            return articuloProveedor.getCostoPedidoArticuloProveedor();
        } catch (Exception e){
            throw new Exception("Error al buscar el costo de pedido: " + e.getMessage(), e);
        }
    }

    //BORRAR: NO SE USA
    public Double obtenerTiempoDemoraPromedioProveedores(Long filtroArticulo) throws Exception {
        try {
            Double tiempoDemoraPromedioProveedores = proveedorArticuloRepository.obtenerTiempoDemoraPromedioProveedores(filtroArticulo);
            return tiempoDemoraPromedioProveedores;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


}
