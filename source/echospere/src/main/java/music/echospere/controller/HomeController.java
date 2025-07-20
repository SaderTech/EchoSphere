package music.echospere.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import music.echospere.entity.Song;
import music.echospere.entity.User;
import music.echospere.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class HomeController {
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final PlaylistRepository playlistRepository;
    private final HttpSession session;

    @GetMapping("/home")
    public String home(Principal principal, Model model) {
        List<Song> songs = songRepository.findAll();

        return "home";
    }

}
