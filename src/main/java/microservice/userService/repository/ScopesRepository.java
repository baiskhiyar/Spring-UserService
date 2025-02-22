package microservice.userService.repository;

import microservice.userService.models.Scopes;
import microservice.userService.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScopesRepository extends JpaRepository<Scopes, Long> {

    @Query("select sc.name from UserRoles ur join RoleScopes rs on ur.roleId = rs.roleId join Scopes sc on rs.scopeId = sc.id where ur.userId = :userId")
    String[] getAllScopesForUser(@Param("userId") int userId);
    Optional<Scopes> findByUsername(String name);
    Optional<Scopes> findById(int userId);
}

