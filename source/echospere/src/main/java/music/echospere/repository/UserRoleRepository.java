package music.echospere.repository;

import music.echospere.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository <UserRole , Integer> {
    // Additional query methods can be defined here if needed
}
