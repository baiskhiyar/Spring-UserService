package microservice.userService.services;

import microservice.userService.models.Scopes;
import microservice.userService.repository.ScopesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScopeService {

    @Autowired
    private ScopesRepository scopesRepository;

    public Scopes addScope(Scopes scope){
        if (checkIfScopeAlreadyAdded(scope.getName())){
            throw new RuntimeException("Scope already Added");
        }
        return scopesRepository.save(scope);
    }

    public boolean checkIfScopeAlreadyAdded(String name){
        return scopesRepository.findByName(name).isPresent();
    }

}

