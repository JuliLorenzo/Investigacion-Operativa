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

    public Double calculoCGI(Double costoAlmacenamiento, Double costoPedido, Double precioArticulo, Double cantidadAComprar, Double demandaAnual) throws Exception {
        try {
            Double costoCompra = precioArticulo * cantidadAComprar;
            Double cgi = costoCompra + costoAlmacenamiento * (cantidadAComprar/2) + costoPedido * (demandaAnual/cantidadAComprar);
            return cgi;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void guardarValorCGI(Double valorCGI, ProveedorArticulo proveedorArticulo) throws Exception{
        proveedorArticulo.setCgiArticulo(valorCGI);
        proveedorArticuloRepository.save(proveedorArticulo);

    }

    @Override
    @Transactional
    public int calculoLoteOptimo(int demandaAnual, double costoPedido, double costoAlmacenamiento) throws Exception {
        //ESTE ES DEL METODO DE TAMAÑO FIJO DE LOTE
        try{
            int loteOptimo = 0;
            loteOptimo = (int)Math.sqrt((2 * demandaAnual * costoPedido) / costoAlmacenamiento);
            return loteOptimo;
        }
        catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public int calculoPuntoPedido(int demandaAnual, double tiempoDemoraProveedor) throws Exception{
        //ESTE ES DEL METODO DE TAMAÑO FIJO DE LOTE
        try {
            int puntoPedido = demandaAnual * (int)Math.round(tiempoDemoraProveedor);
            return puntoPedido;
        } catch(Exception e ){
            throw new Exception(e.getMessage());
        }

    }

    @Override
    @Transactional
    public int calculoStockSeguridad() throws Exception{
        //ESTE ES DEL METODO DE TAMAÑO FIJO DE LOTE
        try {
            return 0; //LO DEJO EN CERO PQ TODAVIA NO SE COMO SE CALCULA xd
        }catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void metodoLoteFijo(Long idArticulo,int demandaAnterior, double costoPedido, double costoAlmacenamiento, double tiempoDemoraProveedor) throws Exception{
        try {
            int loteOptimoCalculado = calculoLoteOptimo(demandaAnterior, costoPedido, costoAlmacenamiento);
            int puntoPedidoCalculado = calculoPuntoPedido(demandaAnterior, tiempoDemoraProveedor);

            ProveedorArticulo proveedorArticulo = proveedorArticuloRepository.findById(idArticulo).orElseThrow(() -> new Exception("Articulo no encontrado"));

            proveedorArticulo.setLoteOptimoArticulo(loteOptimoCalculado);
            proveedorArticulo.setPuntoPedidoArticulo(puntoPedidoCalculado);

        } catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }


}
