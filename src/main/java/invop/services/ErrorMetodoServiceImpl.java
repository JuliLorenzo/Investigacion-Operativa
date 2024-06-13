package invop.services;

import invop.entities.ErrorMetodo;
import invop.repositories.ErrorMetodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ErrorMetodoServiceImpl extends BaseServiceImpl<ErrorMetodo, Long> implements ErrorMetodoService {

    @Autowired
    private ErrorMetodoRepository errorMetodoRepository;

    public ErrorMetodoServiceImpl(ErrorMetodoRepository errorMetodoRepository) {
        super(errorMetodoRepository);
        this.errorMetodoRepository = errorMetodoRepository;
    }
}
