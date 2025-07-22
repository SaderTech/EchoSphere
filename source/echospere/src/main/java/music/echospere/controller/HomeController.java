package music.echospere.controller;

import jakarta.servlet.http.HttpSession; // Giữ nguyên nếu bạn vẫn dùng HttpSession ở đâu đó khác
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
import java.util.ArrayList; // Thêm import này để khởi tạo danh sách rỗng

@Controller
@AllArgsConstructor
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository; // Giữ nguyên repository

    @GetMapping("/home")
    public String home(@RequestParam(name = "filter", defaultValue = "all") String filter,
                       @RequestParam(name = "page", defaultValue = "0") int page,
                       Model model,
                       Principal principal) { // Principal để lấy thông tin người dùng đăng nhập

        int pageSize = 8; // Số lượng item trên mỗi trang
        Pageable pageable = PageRequest.of(page, pageSize);
        model.addAttribute("currentFilter", filter);

        filterDTO filterData = new filterDTO();

        logger.info("Filter: {}, Page: {}", filter, page);

        // Lấy thông tin người dùng hiện tại
        User currentUser = null;
        if (principal != null) {
            currentUser = userRepository.findByUsername(principal.getName()).orElse(null);
        }

        // --- Bắt đầu Switch Case ---
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
                if (currentUser != null) {
                    // Lấy các playlist CỦA NGƯỜI DÙNG hiện tại
                    Page<Playlist> playlistPage = playlistRepository.findByUser(currentUser, pageable);
                    filterData.setPlaylists(playlistPage.getContent());
                    model.addAttribute("page", playlistPage);
                    model.addAttribute("title", "Danh sách phát của bạn");
                    logger.info("Playlists for user '{}' found: {}", currentUser.getUsername(), playlistPage.getContent().size());
                } else {
                    // Nếu không có người dùng, hiển thị thông báo hoặc danh sách rỗng
                    filterData.setPlaylists(new ArrayList<>());
                    model.addAttribute("page", Page.empty(pageable)); // Page rỗng
                    model.addAttribute("title", "Danh sách phát");
                    logger.warn("Attempted to access 'playlists' filter without a logged-in user.");
                }
                break;

            default: // "all"
                model.addAttribute("songs", songRepository.findAll(PageRequest.of(0, 4)).getContent());
                model.addAttribute("albums", albumRepository.findAll(PageRequest.of(0, 4)).getContent());
                model.addAttribute("artists", artistRepository.findAll(PageRequest.of(0, 4)).getContent());

                // --- Cập nhật quan trọng ở đây cho filter "all" ---
                if (currentUser != null) {
                    // Lấy playlist của người dùng hiện tại cho phần "Playlist của bạn" trên trang chủ
                    model.addAttribute("playlists", playlistRepository.findByUser(currentUser, PageRequest.of(0, 4)).getContent());
                    logger.info("User-specific playlists for 'all' filter loaded for user: {}", currentUser.getUsername());
                } else {
                    // Nếu không có người dùng đăng nhập, có thể hiển thị một số playlist công khai
                    // Hoặc đơn giản là một danh sách rỗng
                    model.addAttribute("playlists", new ArrayList<Playlist>()); // Hoặc playlistRepository.findByIsPublic(true, PageRequest.of(0, 4)).getContent()
                    logger.info("No user logged in, 'all' filter showing empty playlists or public ones.");
                }
                // --- Kết thúc cập nhật quan trọng ---

                logger.info("Default view loaded");
                break;
        }
        model.addAttribute("filterData", filterData);

        return "home";
    }
}