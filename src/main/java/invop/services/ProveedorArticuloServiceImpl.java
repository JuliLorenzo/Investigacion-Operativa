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
    public List<Object> findProveedoresByArticulo(String filtroArticulo) throws Exception{
        try {
            List<Object> buscarProveedores = proveedorArticuloRepository.findProveedoresByArticulo(filtroArticulo);
            return buscarProveedores;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<Object> findArticulosByProveedor(String filtroProveedor) throws Exception {
        try {
            List<Object> buscarArticulos = proveedorArticuloRepository.findArticulosByProveedor(filtroProveedor);
            return buscarArticulos;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public List<Object> findArticuloByProveedorDeterminado(List<Object> articulosProveedor, String nombreProveedor) throws Exception {
        try {
            List<Object> articulo = proveedorArticuloRepository.findArticulosByProveedor(nombreProveedor);
            return articulo;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }


    @Override
    @Transactional
    public Long findArticuloByProveedorYArticulo(String nombreArticulo, String nombreProveedor) throws Exception {
        try {
            List<Object> articulos = proveedorArticuloRepository.findProveedoresByArticulo(nombreArticulo);
            List<Object> articuloProveedor = findArticuloByProveedorDeterminado(articulos, nombreProveedor);

            Object articuloEncontrado = articuloProveedor.get(0);


            long costoPedido = articuloEncontrado.get





            return buscarArticulos;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }





}
