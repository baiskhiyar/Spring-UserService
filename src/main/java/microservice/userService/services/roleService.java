package microservice.userService.services;
import microservice.userService.repository.RolesRepository;
import microservice.userService.models.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class roleService {
    @Autowired
    private RolesRepository rolesRepository;

    public Roles addScope(Roles role){
        if (checkIfScopeAlreadyAdded(role.getName())){
            throw new RuntimeException("Scope already Added");
        }
        return rolesRepository.save(role);
    }

    public boolean checkIfScopeAlreadyAdded(String name){
        return rolesRepository.findByUsername(name).isPresent();
    }
}
