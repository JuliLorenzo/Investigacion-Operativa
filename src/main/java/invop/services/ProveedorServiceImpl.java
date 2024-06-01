package invop.services;

import invop.entities.Proveedor;
import invop.repositories.ProveedorRepository;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProveedorServiceImpl extends BaseServiceImpl<Proveedor, Long> implements ProveedorService {

    @Autowired
    public ProveedorServiceImpl(ProveedorRepository proveedorRepository){
        super(proveedorRepository);
    }
}
