package invop.services;

import invop.entities.Articulo;
import invop.entities.OrdenCompra;
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
    public List<ProveedorArticulo> findProveedoresByArticulo(String filtroArticulo) throws Exception{
        try {
            List<ProveedorArticulo> buscarProveedores = proveedorArticuloRepository.findProveedoresByArticulo(filtroArticulo);
            return buscarProveedores;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<ProveedorArticulo> findArticulosByProveedor(String filtroProveedor) throws Exception {
        try {
            List<ProveedorArticulo> buscarArticulos = proveedorArticuloRepository.findArticulosByProveedor(filtroProveedor);
            return buscarArticulos;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public ProveedorArticulo findArticuloDeProveedorDeterminado(List<ProveedorArticulo> articulosDelProveedor, String nombreProveedor) throws Exception {
        try {
            for (ProveedorArticulo proveedorArticulo : articulosDelProveedor){
                if (proveedorArticulo.getProveedor().getNombreProveedor().equals(nombreProveedor)){
                    ProveedorArticulo articuloEncontrado = proveedorArticulo;
                    return articuloEncontrado;
                }
                }
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
        return null;
    }

    //Busca el Costo de Pedido de un ProveedorArticulo.
    @Transactional
    public Double findCostoPedido(String nombreArticulo, String nombreProveedor) throws Exception {
        try {
            List<ProveedorArticulo> articulos = proveedorArticuloRepository.findArticulosByProveedor(nombreProveedor);
            ProveedorArticulo articuloProveedor = findArticuloDeProveedorDeterminado(articulos, nombreProveedor);

            if (articuloProveedor == null) {
                throw new Exception("Artículo no encontrado para el proveedor especificado.");
            }

            return articuloProveedor.getCostoPedidoArticuloProveedor();
        } catch (Exception e){
            throw new Exception("Error al buscar el costo de pedido: " + e.getMessage(), e);
        }
    }

    public Double obtenerTiempoDemoraPromedioProveedores(String filtroArticulo) throws Exception {
        try {
            Double tiempoDemoraPromedioProveedores = proveedorArticuloRepository.obtenerTiempoDemoraPromedioProveedores(filtroArticulo);
            return tiempoDemoraPromedioProveedores;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }





}
