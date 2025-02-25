package microservice.userService.controllers;

import microservice.userService.models.RoleScopes;
import microservice.userService.services.RoleSocpesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("spring/roleScope")
public class RoleScopesController {

    @Autowired
    RoleSocpesService roleSocpesService;

    @PutMapping("addRoleScope")
    public RoleScopes adduserRoles(@RequestBody RoleScopes rolescopes) {
        return roleSocpesService.addRoleScope(rolescopes);
    }

}
