package music.echospere.repository;

import music.echospere.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Đảm bảo import Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    // Thay đổi kiểu của tham số thứ hai từ PageRequest thành Pageable
    Page<Artist> findByNameContainingIgnoreCase(String query, Pageable pageable);
}