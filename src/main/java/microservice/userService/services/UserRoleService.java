package microservice.userService.services;

import microservice.userService.models.UserRoles;
import microservice.userService.repository.UserRolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {
    @Autowired
    private UserRolesRepository userRolesRepository;

    public UserRoles addUserRoles(UserRoles userRoles) {

        return userRolesRepository.save(userRoles);
    }
    public int[] getRoleIdsForUser(int userId) {
        return userRolesRepository.getRoleIdsForUser(userId);
    }
}
