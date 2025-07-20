package music.echospere.repository;

import music.echospere.entity.Playlist;
import music.echospere.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    Page<Playlist> findByUser(User user, Pageable pageable);
}
