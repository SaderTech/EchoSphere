package music.echospere.repository;

import music.echospere.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Thay đổi import từ PageRequest sang Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {
    // Thay đổi kiểu của tham số thứ hai từ PageRequest thành Pageable
    Page<Album> findByTitleContainingIgnoreCase(String query, Pageable pageable);
}