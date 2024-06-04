package invop.services;

import invop.entities.Articulo;
import invop.repositories.ArticuloRepository;
import invop.repositories.BaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticuloServiceImpl extends BaseServiceImpl<Articulo, Long> implements ArticuloService {

    @Autowired
    private ArticuloRepository articuloRepository;
    public ArticuloServiceImpl(BaseRepository<Articulo, Long> baseRepository, ArticuloRepository articuloRepository) {
        super(baseRepository);
        this.articuloRepository = articuloRepository;
    }


    @Override
    @Transactional
    public int calculoLoteOptimo(int demandaAnterior, double costoPedido, double costoAlmacenamiento) throws Exception {
        //ESTE ES DEL METODO DE TAMAÑO FIJO DE LOTE
        try{
            int loteOptimo = 0;
            loteOptimo = (int)Math.sqrt((2 * demandaAnterior * costoPedido) / costoAlmacenamiento);
            return loteOptimo;
        }
        catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public int calculoPuntoPedido(int demandaAnterior, double tiempoDemoraProveedor) throws Exception{
       //ESTE ES DEL METODO DE TAMAÑO FIJO DE LOTE
        try {
            int puntoPedido = demandaAnterior * (int)Math.round(tiempoDemoraProveedor);
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

            Articulo articulo = articuloRepository.findById(idArticulo).orElseThrow(() -> new Exception("Articulo no encontrado"));

            articulo.setLoteOptimoArticulo(loteOptimoCalculado);
            articulo.setPuntoPedidoArticulo(puntoPedidoCalculado);

        } catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }

}
