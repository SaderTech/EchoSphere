package music.echospere.repository;

import music.echospere.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
    List<Song> findByTitleContainingIgnoreCase(String title);

    // SỬA LẠI PHƯƠNG THỨC TÌM KIẾM
    // Thêm LEFT JOIN FETCH để tải các artist liên quan trong cùng một truy vấn,
    // giải quyết triệt để LazyInitializationException.
    @Query("SELECT DISTINCT s FROM Song s " +
           "LEFT JOIN FETCH s.artists sa " +
           "LEFT JOIN FETCH sa.artist a " +
           "WHERE " +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Song> searchByTitleOrArtistName(@Param("query") String query);

    @Query("SELECT s FROM Song s " +
            "LEFT JOIN FETCH s.album a " +
            "LEFT JOIN FETCH s.artists sa " +
            "LEFT JOIN FETCH sa.artist art " +
            "WHERE s.id = :id")
    Optional<Song> findByIdWithDetails(@Param("id") Integer id);

    // Phương thức để lấy danh sách bài hát trong playlist (dùng cho /api/{playlistId}/songs)
    @Query("SELECT DISTINCT s FROM Playlist p JOIN p.songs ps JOIN ps.song s " +
           "LEFT JOIN FETCH s.album a " +
           "LEFT JOIN FETCH s.artists sa " +
           "LEFT JOIN FETCH sa.artist art " +
           "WHERE p.id = :playlistId")
    Set<Song> findSongsByPlaylistIdWithAlbumAndArtist(@Param("playlistId") Integer playlistId);
}