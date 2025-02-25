package microservice.userService.controllers;


import microservice.userService.models.Roles;
import microservice.userService.models.UserRoles;
import microservice.userService.services.RoleService;
import microservice.userService.services.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("spring/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("addRole")
    public Roles addRoles(@RequestBody Roles role){
        return roleService.addRole(role);
    }

}
