package microservice.userService.repository;

import microservice.userService.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> { // Users class, long -> type of users primary key.
    Optional<Users> findByUsername(String username);
    Optional<Users> findById(int userId);
}

