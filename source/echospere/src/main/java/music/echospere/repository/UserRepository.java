package music.echospere.repository;

import music.echospere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    // Additional query methods can be defined here if needed
}
