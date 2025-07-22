package music.echospere.repository;

import music.echospere.entity.Playlist;
import music.echospere.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    Page<Playlist> findByUser(User user, Pageable pageable);
    List<Playlist> findByUser(User user);

    // Thêm phương thức này để tải playlist cùng với thông tin người dùng (user)
    @Query("SELECT p FROM Playlist p JOIN FETCH p.user WHERE p.id = :id")
    Optional<Playlist> findByIdWithUser(@Param("id") Integer id);

    Page<Playlist> findByUserAndNameContainingIgnoreCase(User currentUser, String query, Pageable pageable);
}