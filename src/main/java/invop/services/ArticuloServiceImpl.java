package invop.services;

import invop.entities.Articulo;
import invop.repositories.ArticuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticuloServiceImpl extends BaseServiceImpl<Articulo, Long> implements ArticuloService {

    @Autowired
    public ArticuloServiceImpl(ArticuloRepository articuloRepository) {
        super(articuloRepository);
    }

}
