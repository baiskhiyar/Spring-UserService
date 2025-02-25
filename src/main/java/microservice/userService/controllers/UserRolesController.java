package microservice.userService.controllers;

import microservice.userService.models.UserRoles;
import microservice.userService.services.UserRoleService;
import microservice.userService.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("spring/userRole")
public class UserRolesController {

    @Autowired
    private UserRoleService userRoleService;

    @PutMapping("addUserRole")
    public UserRoles adduserRoles(@RequestBody UserRoles userRoles) {
        return userRoleService.addUserRoles(userRoles);
    }
}
