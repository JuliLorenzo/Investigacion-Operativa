package invop.services;

import invop.entities.Articulo;
import invop.entities.OrdenCompra;
import invop.entities.OrdenCompraDetalle;
import invop.repositories.ArticuloRepository;
import invop.repositories.BaseRepository;
import invop.repositories.OrdenCompraRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ArticuloServiceImpl extends BaseServiceImpl<Articulo, Long> implements ArticuloService {

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private OrdenCompraService ordenCompraService;


    public ArticuloServiceImpl(BaseRepository<Articulo, Long> baseRepository, ArticuloRepository articuloRepository, OrdenCompraService ordenCompraService) {
        super(baseRepository);
        this.articuloRepository = articuloRepository;
        this.ordenCompraService = ordenCompraService;
    }






    //Controla que el Articulo no tenga Ordenes de Compras Activas.
    public boolean controlOrdenCompraActiva(Long idArticulo) throws Exception{
            boolean ordenActiva = ordenCompraService.articuloConOrdenActiva(idArticulo);
            return ordenActiva;
    }

    //Borrar articulo si no hay orden de compra activa
    public void darDeBajaArticulo(Long idArticulo) throws Exception{
        boolean ordenActiva = controlOrdenCompraActiva(idArticulo);
        try{
            if(!ordenActiva){
                Articulo articuloABorrar = articuloRepository.findById(idArticulo).orElseThrow(() -> new EntityNotFoundException("Articulo no encontrado"));
                articuloRepository.delete(articuloABorrar);
            }
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }




}
