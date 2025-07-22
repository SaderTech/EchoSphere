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
import java.util.Collections;

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
                       @RequestParam(name = "query", required = false) String query,
                       Model model,
                       Principal principal) { // Principal để lấy thông tin người dùng đăng nhập

        int pageSize = 8; // Số lượng item trên mỗi trang
        Pageable pageable = PageRequest.of(page, pageSize);
        model.addAttribute("currentFilter", filter);
        model.addAttribute("query", query);

        filterDTO filterData = new filterDTO();

        logger.info("Filter: {}, Page: {}", filter, page, query);

        // Lấy thông tin người dùng hiện tại
        User currentUser = null;
        if (principal != null) {
            currentUser = userRepository.findByUsername(principal.getName()).orElse(null);
        }

        // --- Bắt đầu Switch Case ---

        switch (filter) {
            case "songs":
                Page<Song> songPage;
                if (query != null && !query.trim().isEmpty()) {
                    // Tìm kiếm bài hát theo từ khóa nếu có query
                    songPage = songRepository.findByTitleContainingIgnoreCase(query, pageable);
                    model.addAttribute("title", "Kết quả tìm kiếm Bài hát cho: '" + query + "'");
                    logger.info("Searching songs for query: '{}', found: {}", query, songPage.getContent().size());
                } else {
                    // Lấy tất cả bài hát nếu không có query
                    songPage = songRepository.findAll(pageable);
                    model.addAttribute("title", "Bài hát");
                    logger.info("All songs found: {}", songPage.getContent().size());
                }
                filterData.setSongs(songPage.getContent());
                model.addAttribute("page", songPage);
                break;

            case "albums":
                Page<Album> albumPage;
                if (query != null && !query.trim().isEmpty()) {
                    // Tìm kiếm album theo từ khóa nếu có query
                    albumPage = albumRepository.findByTitleContainingIgnoreCase(query, pageable);
                    model.addAttribute("title", "Kết quả tìm kiếm Album cho: '" + query + "'");
                    logger.info("Searching albums for query: '{}', found: {}", query, albumPage.getContent().size());
                } else {
                    // Lấy tất cả album nếu không có query
                    albumPage = albumRepository.findAll(pageable);
                    model.addAttribute("title", "Album");
                    logger.info("All albums found: {}", albumPage.getContent().size());
                }
                filterData.setAlbums(albumPage.getContent());
                model.addAttribute("page", albumPage);
                break;

            case "artists":
                Page<Artist> artistPage;
                if (query != null && !query.trim().isEmpty()) {
                    // Tìm kiếm nghệ sĩ theo từ khóa nếu có query
                    artistPage = artistRepository.findByNameContainingIgnoreCase(query, pageable);
                    model.addAttribute("title", "Kết quả tìm kiếm Nghệ sĩ cho: '" + query + "'");
                    logger.info("Searching artists for query: '{}', found: {}", query, artistPage.getContent().size());
                } else {
                    // Lấy tất cả nghệ sĩ nếu không có query
                    artistPage = artistRepository.findAll(pageable);
                    model.addAttribute("title", "Nghệ sĩ");
                    logger.info("All artists found: {}", artistPage.getContent().size());
                }
                filterData.setArtists(artistPage.getContent());
                model.addAttribute("page", artistPage);
                break;

            case "playlists":
                Page<Playlist> playlistPage;
                if (currentUser != null) {
                    if (query != null && !query.trim().isEmpty()) {
                        // Tìm kiếm playlist CỦA NGƯỜI DÙNG hiện tại theo từ khóa
                        playlistPage = playlistRepository.findByUserAndNameContainingIgnoreCase(currentUser, query, pageable);
                        model.addAttribute("title", "Kết quả tìm kiếm Danh sách phát của bạn cho: '" + query + "'");
                        logger.info("Searching user playlists for query: '{}', found: {}", query, playlistPage.getContent().size());
                    } else {
                        // Lấy tất cả playlist CỦA NGƯỜI DÙNG hiện tại
                        playlistPage = playlistRepository.findByUser(currentUser, pageable);
                        model.addAttribute("title", "Danh sách phát của bạn");
                        logger.info("All user playlists for '{}' found: {}", currentUser.getUsername(), playlistPage.getContent().size());
                    }
                } else {
                    // Nếu không có người dùng đăng nhập, hoặc không tìm thấy, hiển thị danh sách rỗng
                    // Hoặc bạn có thể tìm kiếm các playlist công khai nếu có.
                    playlistPage = Page.empty(pageable); // Page rỗng
                    model.addAttribute("title", "Danh sách phát");
                    logger.warn("Attempted to access 'playlists' filter without a logged-in user or no public playlists found.");
                }
                filterData.setPlaylists(playlistPage.getContent());
                model.addAttribute("page", playlistPage);
                break;

            default: // "all"
                if (query != null && !query.trim().isEmpty()) {
                    // Nếu có query trong chế độ "all", tìm kiếm trên tất cả các loại
                    model.addAttribute("songs", songRepository.findByTitleContainingIgnoreCase(query, PageRequest.of(0, 4)).getContent());
                    model.addAttribute("albums", albumRepository.findByTitleContainingIgnoreCase(query, PageRequest.of(0, 4)).getContent());
                    model.addAttribute("artists", artistRepository.findByNameContainingIgnoreCase(query, PageRequest.of(0, 4)).getContent());
                    // Có thể thêm tìm kiếm playlist công khai ở đây nếu có logic isPublic
                    model.addAttribute("playlists", Collections.emptyList()); // Mặc định rỗng hoặc tìm kiếm public
                    model.addAttribute("title", "Kết quả tìm kiếm cho: '" + query + "'");
                    logger.info("Searching all categories for query: '{}'", query);
                } else {
                    // Chế độ "all" mặc định (trending, featured)
                    model.addAttribute("songs", songRepository.findAll(PageRequest.of(0, 4)).getContent());
                    model.addAttribute("albums", albumRepository.findAll(PageRequest.of(0, 4)).getContent());
                    model.addAttribute("artists", artistRepository.findAll(PageRequest.of(0, 4)).getContent());

                    if (currentUser != null) {
                        model.addAttribute("playlists", playlistRepository.findByUser(currentUser, PageRequest.of(0, 4)).getContent());
                        logger.info("User-specific playlists for 'all' filter loaded for user: {}", currentUser.getUsername());
                    } else {
                        model.addAttribute("playlists", new ArrayList<Playlist>());
                        logger.info("No user logged in, 'all' filter showing empty playlists.");
                    }
                    model.addAttribute("title", "Trang chủ");
                    logger.info("Default view loaded");
                }
                break;
        }
        model.addAttribute("filterData", filterData);

        return "home";
    }
}