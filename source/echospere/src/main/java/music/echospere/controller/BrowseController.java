package music.echospere.controller;

import lombok.AllArgsConstructor;
import music.echospere.entity.User;
import music.echospere.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class BrowseController {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private static final int PAGE_SIZE = 12;

    @GetMapping("/browse")
    public String browse(
            @RequestParam("type") String type,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model, Principal principal) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        switch (type) {
            case "songs":
                model.addAttribute("pageData", songRepository.findAll(pageable));
                model.addAttribute("browseType", "songs");
                break;
            case "albums":
                model.addAttribute("pageData", albumRepository.findAll(pageable));
                model.addAttribute("browseType", "albums");
                break;
            case "artists":
                model.addAttribute("pageData", artistRepository.findAll(pageable));
                model.addAttribute("browseType", "artists");
                break;
            case "playlists":
                if (principal != null) {
                    User currentUser = userRepository.findByEmail(principal.getName()).orElse(null);
                    if (currentUser != null) {
                        model.addAttribute("pageData", playlistRepository.findByUser(currentUser, pageable));
                    }
                }
                model.addAttribute("browseType", "playlists");
                break;
            default:
                // Trả về một fragment rỗng hoặc lỗi nếu type không hợp lệ
                return "fragments/browse-results :: empty";
        }

        // Trả về đường dẫn đến fragment
        return "fragments/browse-results :: content";
    }
}
