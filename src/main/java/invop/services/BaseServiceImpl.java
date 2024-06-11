package invop.services;

import invop.entities.Base;
import invop.repositories.BaseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.*;



public abstract class BaseServiceImpl <E extends Base, ID extends Serializable> implements BaseService<E, ID> {

    protected BaseRepository<E, ID> baseRepository;

    public BaseServiceImpl(BaseRepository<E, ID> baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Override
    @Transactional
    public E save(E entity) throws Exception {
        try{
            entity = baseRepository.save(entity);
            return entity;
        }
        catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public E update(ID id, E entity) throws Exception {
        try{
            Optional<E> entityOptional = baseRepository.findById((id));
            E entidadExistente = entityOptional.orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));
            BeanUtils.copyProperties(entity, entidadExistente, getNullPropertyNames(entity));
            E entityUpdate = entityOptional.get();
           return baseRepository.save(entidadExistente);
        }
        catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean delete(ID id) throws Exception {
        try{
            if(baseRepository.existsById(id)){
                baseRepository.deleteById(id);
                return true;
            }
            else {
                throw new Exception();
            }
        }
        catch(Exception e ){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional // son transacciones con la BD
    public List<E> findAll() throws Exception {
        try {
            List<E> entities = baseRepository.findAll();
            return entities;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public E findById(ID id) throws Exception {
        try {
            Optional<E> entityOptional = baseRepository.findById(id);
            return entityOptional.get();
            // obtiene una entidad si la encuentra, y sino larga excepcion
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    // Método para obtener los nombres de las propiedades nulas
    private String[] getNullPropertyNames(E source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }



}
