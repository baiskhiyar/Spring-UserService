package microservice.userService.controllers;


import microservice.userService.models.Roles;
import microservice.userService.models.Scopes;
import microservice.userService.services.ScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("spring/scope")
public class ScopeController {

    @Autowired
    private ScopeService scopeService;

    @PostMapping("addRole")
    public Scopes addRoles(@RequestBody Scopes scope){
        return scopeService.addScope(scope);
    }

}
