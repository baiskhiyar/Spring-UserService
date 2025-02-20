package microservice.userService.repository;

import microservice.userService.models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Roles, Integer> {
}
