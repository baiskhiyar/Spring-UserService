package microservice.userService.services;

import microservice.userService.models.RoleScopes;
import microservice.userService.repository.RoleScopesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleSocpesService {

    @Autowired
    private RoleScopesRepository roleScopesRepository;

    public RoleScopes addRoleScope(RoleScopes roleScopes) {
        return roleScopesRepository.save(roleScopes);
    }
}
