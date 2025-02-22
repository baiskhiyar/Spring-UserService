package microservice.userService.repository;

import microservice.userService.models.RoleScopes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleScopesRepository extends JpaRepository<RoleScopes, Integer> {
}
