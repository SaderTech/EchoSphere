package music.echospere.repository;

import music.echospere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    // Additional query methods can be defined here if needed
}