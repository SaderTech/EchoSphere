package music.echospere.repository;

import music.echospere.entity.PlaylistSong;
import music.echospere.entity.PlaylistSongId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, PlaylistSongId> {
}
