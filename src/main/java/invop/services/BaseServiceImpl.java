package invop.services;

import invop.entities.Base;

import java.io.Serializable;

public abstract class BaseServiceImpl <E extends Base, ID extends Serializable> implements BaseService<E, ID> {
}
