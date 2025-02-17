package microservice.userService.helpers;

import microservice.userService.repository.ScopesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScopesHelper {

    @Autowired
    private ScopesRepository scopesRepository;

    public String[] getScopesForUser(int userId){
        return scopesRepository.getAllScopesForUser(userId);
    }
}
