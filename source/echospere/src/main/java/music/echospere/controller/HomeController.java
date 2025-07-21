package music.echospere.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import music.echospere.DTO.filterDTO;
import music.echospere.entity.*;
import music.echospere.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;

    @GetMapping("/home")
    public String home(@RequestParam(name = "filter", defaultValue = "all") String filter,
                       @RequestParam(name = "page", defaultValue = "0") int page,
                       Model model,
                       Principal principal) {

        int pageSize = 8; // Số lượng item trên mỗi trang
        Pageable pageable = PageRequest.of(page, pageSize);
        model.addAttribute("currentFilter", filter);

        filterDTO filterData = new filterDTO();

        logger.info("Filter: {}, Page: {}", filter, page);

        switch (filter) {
            case "songs":
                Page<Song> songPage = songRepository.findAll(pageable);
                filterData.setSongs(songPage.getContent());
                model.addAttribute("page", songPage);
                model.addAttribute("title", "Bài hát");
                logger.info("Songs found: {}", songPage.getContent().size());
                break;

            case "albums":
                Page<Album> albumPage = albumRepository.findAll(pageable);
                filterData.setAlbums(albumPage.getContent());
                model.addAttribute("page", albumPage);
                model.addAttribute("title", "Album");
                logger.info("Albums found: {}", albumPage.getContent().size());
                break;

            case "artists":
                Page<Artist> artistPage = artistRepository.findAll(pageable);
                filterData.setArtists(artistPage.getContent());
                model.addAttribute("page", artistPage);
                model.addAttribute("title", "Nghệ sĩ");
                logger.info("Artists found: {}", artistPage.getContent().size());
                break;

            case "playlists":
                if (principal != null) {
                    userRepository.findByUsername(principal.getName()).ifPresent(user -> {
                        Page<Playlist> playlistPage = playlistRepository.findByUser(user, pageable);
                        filterData.setPlaylists(playlistPage.getContent());
                        model.addAttribute("page", playlistPage);
                        model.addAttribute("title", "Danh sách phát của bạn");
                        logger.info("Playlists found: {}", playlistPage.getContent().size());
                    });
                }
                break;

            default: // "all"
                model.addAttribute("songs", songRepository.findAll(PageRequest.of(0, 4)).getContent());
                model.addAttribute("albums", albumRepository.findAll(PageRequest.of(0, 4)).getContent());
                model.addAttribute("artists", artistRepository.findAll(PageRequest.of(0, 4)).getContent());
                model.addAttribute("playlists", playlistRepository.findAll(PageRequest.of(0, 4)).getContent());
                logger.info("Default view loaded");
                break;
        }
        model.addAttribute("filterData", filterData);

        return "home";
    }
}