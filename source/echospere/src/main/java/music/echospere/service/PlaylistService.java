package music.echospere.service;

import music.echospere.entity.*;
import music.echospere.repository.PlaylistRepository;
import music.echospere.repository.PlaylistSongRepository;
import music.echospere.repository.SongRepository; // Cần SongRepository để tìm Song
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Quan trọng cho các thao tác với Set

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set; // Để làm việc với Set<Song>
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;// Cần inject SongRepository
    private final PlaylistSongRepository playlistSongRepository; // Nếu bạn cần quản lý mối quan hệ giữa Playlist và Song
    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository, SongRepository songRepository, PlaylistSongRepository playlistSongRepository) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.playlistSongRepository = playlistSongRepository;
    }

    @Transactional // Đảm bảo mọi thay đổi được lưu
    public Playlist savePlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    public Optional<Playlist> getPlaylistById(Integer id) {
        // Có thể cần fetch join songs nếu bạn muốn truy cập chúng ngay lập tức mà không có LazyInitializationException
        // Nếu không, hãy đảm bảo thao tác truy cập songs diễn ra trong một transaction.
        return playlistRepository.findById(id);
    }

    public List<Playlist> findByUser(User user) {
        return playlistRepository.findByUser(user);
    }

    @Transactional
    public boolean addSongToPlaylist(Integer playlistId, int songId) {
        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);
        Optional<Song> songOpt = songRepository.findById(songId); // Dùng SongRepository

        if (playlistOpt.isPresent() && songOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            Song song = songOpt.get();
            PlaylistSongId psId = new PlaylistSongId();
            psId.setPlaylistId(playlistId);
            psId.setSongId(songId);

            // Check if the association already exists
            if (playlistSongRepository.findById(psId).isEmpty()) {
                PlaylistSong playlistSong = new PlaylistSong();
                playlistSong.setId(psId);
                playlistSong.setPlaylist(playlist);
                playlistSong.setSong(song);
                playlistSong.setDateAdded(Instant.now()); // Set date added

                playlistSongRepository.save(playlistSong); // Save the association
                return true;
            } else {
                return false; // Song already in playlist
            }
        }
        return false; // Playlist or song not found
    }

    @Transactional
    public boolean removeSongFromPlaylist(Integer playlistId, int songId) { // songId is Long
        PlaylistSongId psId = new PlaylistSongId();
        psId.setPlaylistId(playlistId);
        psId.setSongId(songId);

        if (playlistSongRepository.existsById(psId)) {
            playlistSongRepository.deleteById(psId); // Delete the association directly
            return true;
        }
        return false; // Association not found
    }

    @Transactional(readOnly = true) // Read-only transaction for fetching
    public Set<Song> getSongsForPlaylist(Integer playlistId) {
        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);
        if (playlistOpt.isPresent()) {
            // Because playlistSongs is Lazy, we need to be in a transaction
            // or use a fetch join query in the repository.
            // Using .stream().map() to extract Song objects from PlaylistSong entities
            return playlistOpt.get().getSongs().stream()
                    .map(PlaylistSong::getSong)
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }
}