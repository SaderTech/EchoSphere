package music.echospere.service;

import lombok.AllArgsConstructor;
import music.echospere.entity.Song;
import music.echospere.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor // AllArgsConstructor sẽ tự động tạo constructor và inject SongRepository
public class SongService {

    private final SongRepository songRepository; // Sử dụng final để đảm bảo DI

    public List<Song> searchSongs(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList(); // Trả về danh sách rỗng nếu query trống
        }
        // Gọi phương thức đã được sửa trong repository
        return songRepository.searchByTitleOrArtistName(query);
    }

    public Optional<Song> getSongById(int id) {
        return songRepository.findById(id);
    }
}