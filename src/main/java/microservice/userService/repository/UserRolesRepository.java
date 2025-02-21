package microservice.userService.repository;

import microservice.userService.models.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {

    @Query("SELECT ur.roleId FROM UserRoles ur WHERE ur.userId = :userId")
    int[] getRoleIdsForUser (@Param("userId") int userId);
}

