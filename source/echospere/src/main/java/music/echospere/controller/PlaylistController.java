package music.echospere.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import music.echospere.DTO.PlaylistFormDTO;
import music.echospere.entity.Playlist;
import music.echospere.entity.Song;
import music.echospere.entity.User;
import music.echospere.repository.PlaylistRepository;
import music.echospere.repository.SongRepository;
import music.echospere.repository.UserRepository;
import music.echospere.service.PlaylistService;
import music.echospere.service.SongService;
import music.echospere.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping("/playlists")
public class PlaylistController {
    private static final Logger logger = LoggerFactory.getLogger(PlaylistController.class);
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final PlaylistService playlistService;
    private final SongService songService;
    private static final String UPLOAD_DIR = "static/playlistImage/"; // Consider making this configurable
    private final UserService userService;
    private final SongRepository songRepository;

    @GetMapping
    public String showHome(Model model, Principal principal) {
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName()).orElse(null);
            if (user != null) {
                List<Playlist> playlists = playlistRepository.findByUser(user);
                model.addAttribute("playlists", playlists);
            }
        }
        // Thêm logic khác cho home
        return "home";
    }

    @GetMapping("/add")
    public String showAddPlaylistForm(Model model) {
        model.addAttribute("playlistFormDTO", new PlaylistFormDTO());
        model.addAttribute("isEdit", false); // Explicitly set isEdit to false
        return "createPlaylits";
    }

    @PostMapping("/add")
    public String addPlaylist(@Valid @ModelAttribute("playlistFormDTO") PlaylistFormDTO playlistFormDTO, // Use @ModelAttribute
                              BindingResult bindingResult,
                              @RequestParam("coverImage") MultipartFile coverImage,
                              Principal principal,
                              Model model){
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors: {}", bindingResult.getAllErrors());
            model.addAttribute("isEdit", false); // Re-add isEdit if validation fails
            return "createPlaylits";
        }

        if(principal == null){
            model.addAttribute("errorMessage", "Bạn cần đăng nhập để tạo playlist");
            model.addAttribute("isEdit", false); // Re-add isEdit if not logged in
            logger.warn("User not logged in");
            return "createPlaylits";
        }

        try{
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Playlist playlist = new Playlist();
            playlist.setName(playlistFormDTO.getNamePlaylist());
            playlist.setUser(user);
            playlist.setIsPublic(playlistFormDTO.getIsPublic());
            if(!coverImage.isEmpty()){
                String imageUrl = saveImage(coverImage);
                playlist.setPlaylistUrl(imageUrl);
            }else if (playlistFormDTO.getPlaylistUrl() != null && !playlistFormDTO.getPlaylistUrl().isEmpty()) {
                playlist.setPlaylistUrl(playlistFormDTO.getPlaylistUrl());
            }
            playlistRepository.save(playlist);
            // After successful creation, redirect to home with a success message
            // Flash attributes are ideal for one-time messages after a redirect
            // model.addAttribute("successMessage", "Playlist đã được tạo thành công"); // This won't show after redirect
            logger.info("Playlist created successfully: {}", playlist.getName());
            return "redirect:/home?filter=playlists";
        }catch (Exception e){
            model.addAttribute("errorMessage", "Lỗi khi tạo playlist: " + e.getMessage());
            model.addAttribute("isEdit", false); // Re-add isEdit on error
            logger.error("Error when creating playlist: {}", e.getMessage());
            return "createPlaylits";
        }
    }

    private String saveImage(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            // It's often better to return null or a default image path here
            // rather than throwing an IllegalArgumentException, depending on your UI flow.
            // For now, keeping it as is based on previous context.
            throw new IllegalArgumentException("File hình ảnh không được rỗng");
        }

        // Định nghĩa thư mục lưu trữ
        // Ensure this UPLOAD_DIR matches the base path used in handlePlaylistCoverImage
        String uploadDir = UPLOAD_DIR; // Using the class constant
        Path uploadPath = Paths.get(uploadDir);

        // Tạo thư mục nếu chưa tồn tại
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file duy nhất
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Lưu file
        Files.write(filePath, file.getBytes());

        // Trả về URL tương đối mà web server có thể phục vụ
        // This should align with your static resource mapping (e.g., /static/playlistImage/ -> /playlistImage/)
        return "/playlistImage/" + fileName;
    }

    @GetMapping("/edit/{id}") // GET /playlists/edit/{id}
    public String showEditPlaylistForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes, Principal principal) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập để chỉnh sửa playlist.");
            return "redirect:/login";
        }

        Optional<Playlist> playlistOpt = playlistService.getPlaylistById(id);
        if (playlistOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy playlist.");
            logger.warn("Attempted to edit non-existent playlist with ID: {}", id);
            return "redirect:/playlists";
        }

        Playlist playlist = playlistOpt.get();
        try {
            User currentUser = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

            // Kiểm tra quyền: chỉ chủ sở hữu playlist mới được chỉnh sửa
            if (!playlist.getUser().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền chỉnh sửa playlist này.");
                logger.warn("User {} attempted to edit playlist {} owned by different user {}.", currentUser.getUsername(), id, playlist.getUser().getUsername());
                return "redirect:/playlists"; // Hoặc trang lỗi 403
            }

            PlaylistFormDTO playlistFormDTO = new PlaylistFormDTO();
            playlistFormDTO.setId(playlist.getId()); // Make sure ID is set for edit
            playlistFormDTO.setNamePlaylist(playlist.getName());
            playlistFormDTO.setPlaylistUrl(playlist.getPlaylistUrl());
            playlistFormDTO.setIsPublic(playlist.getIsPublic());

            // SỬA ĐỔI: Đổi tên attribute từ "playlistForm" thành "playlistFormDTO" để nhất quán
            model.addAttribute("playlistFormDTO", playlistFormDTO);
            model.addAttribute("currentPlaylist", playlist); // Để hiển thị bài hát hiện tại
            model.addAttribute("isEdit", true); // Báo hiệu đây là chế độ chỉnh sửa
            return "createPlaylits"; // Vẫn dùng chung template

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi xác thực quyền: " + e.getMessage());
            logger.error("Error during playlist edit form access for ID {}: {}", id, e.getMessage());
            return "redirect:/playlists";
        }
    }

    @PostMapping("/edit/{id}") // POST /playlists/edit/{id}
    public String updatePlaylist(@PathVariable Integer id,
                                 // SỬA ĐỔI: Thay @ModelAttribute("playlistForm") thành @ModelAttribute("playlistFormDTO")
                                 @Valid @ModelAttribute("playlistFormDTO") PlaylistFormDTO playlistFormDTO,
                                 BindingResult bindingResult,
                                 @RequestParam(value = "coverImage", required = false) MultipartFile coverImage, // required = false vì có thể không update ảnh
                                 Principal principal,
                                 RedirectAttributes redirectAttributes,
                                 Model model) { // Thêm Model để truyền lại currentPlaylist nếu có lỗi
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors during playlist update for ID {}: {}", id, bindingResult.getAllErrors());
            // Nếu có lỗi validation, cần truyền lại currentPlaylist để hiển thị bài hát đã có
            Optional<Playlist> playlistOpt = playlistService.getPlaylistById(id);
            if(playlistOpt.isPresent()){
                model.addAttribute("currentPlaylist", playlistOpt.get());
            }
            model.addAttribute("isEdit", true); // ENSURE THIS IS PRESENT if validation fails on edit
            // Spring will automatically put playlistFormDTO back into the model due to @ModelAttribute
            return "createPlaylits";
        }

        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập để cập nhật playlist.");
            return "redirect:/login";
        }

        try {
            User currentUser = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

            Optional<Playlist> existingPlaylistOpt = playlistService.getPlaylistById(id);
            if (existingPlaylistOpt.isPresent()) {
                Playlist existingPlaylist = existingPlaylistOpt.get();

                // Kiểm tra quyền: chỉ chủ sở hữu playlist mới được chỉnh sửa
                if (!existingPlaylist.getUser().getId().equals(currentUser.getId())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền cập nhật playlist này.");
                    logger.warn("User {} attempted to update playlist {} owned by different user {}.", currentUser.getUsername(), id, existingPlaylist.getUser().getUsername());
                    return "redirect:/playlists";
                }

                existingPlaylist.setName(playlistFormDTO.getNamePlaylist());
                existingPlaylist.setIsPublic(playlistFormDTO.getIsPublic());

                String newImageUrl = handlePlaylistCoverImage(coverImage, playlistFormDTO.getPlaylistUrl());
                if (newImageUrl != null) {
                    existingPlaylist.setPlaylistUrl(newImageUrl);
                }

                playlistService.savePlaylist(existingPlaylist); // Sử dụng playlistService
                redirectAttributes.addFlashAttribute("successMessage", "Playlist đã được cập nhật thành công!");
                logger.info("Playlist updated successfully: {}", existingPlaylist.getName());
                // Redirect back to the edit page to show success message and updated info
                return "redirect:/playlists/edit/" + id;
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy playlist để cập nhật.");
                logger.warn("Attempted to update non-existent playlist with ID: {}", id);
                return "redirect:/playlists";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật playlist: " + e.getMessage());
            logger.error("Error when updating playlist ID {}: {}", id, e.getMessage(), e);
            // On error during update, redirect back to the edit page to preserve path/ID
            return "redirect:/playlists/edit/" + id;
        }
    }

    private String handlePlaylistCoverImage(MultipartFile coverImage, String playlistUrlFromForm) throws IOException {
        String finalImageUrl = null;
        if (coverImage != null && !coverImage.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = UUID.randomUUID().toString() + "_" + coverImage.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(coverImage.getInputStream(), filePath);
                // Adjust this path to match how your static resources are served
                finalImageUrl = "/playlistImage/" + fileName; // Assumes /static/playlistImage/ maps to /playlistImage/
                logger.debug("Saved new cover image: {}", finalImageUrl);
            } catch (IOException e) {
                logger.error("Failed to save cover image file: {}", e.getMessage(), e);
                throw e; // Re-throw to be caught by calling method
            }
        } else if (playlistUrlFromForm != null && !playlistUrlFromForm.isEmpty()) {
            // Only use URL from form if it's not a default placeholder
            if (!playlistUrlFromForm.contains("default.png") && !playlistUrlFromForm.contains("placeholder.com")) {
                finalImageUrl = playlistUrlFromForm; // Sử dụng URL nếu được cung cấp
                logger.debug("Using playlist URL from form: {}", finalImageUrl);
            }
        } else {
            // Có thể đặt ảnh mặc định nếu không có cả file và URL
            finalImageUrl = "/playlistImage/default.png"; // Đảm bảo có ảnh default.png trong static/playlistImage/
            logger.debug("No cover image provided, using default: {}", finalImageUrl);
        }
        return finalImageUrl;
    }


    // --- API endpoints cho AJAX (để tìm kiếm, thêm/xóa bài hát) ---
    // Cần thêm @RestController hoặc @ResponseBody cho các phương thức này
    // để Spring biết rằng chúng trả về dữ liệu JSON/XML chứ không phải view name.
    @GetMapping("/api/songs/search")
    @ResponseBody // Trả về JSON trực tiếp
    public ResponseEntity<List<Song>> searchSongs(@RequestParam String query) {
        logger.info("Searching songs with query: {}", query);
        List<Song> songs = songService.searchSongs(query);
        return ResponseEntity.ok(songs);
    }

    @PostMapping("/api/songs/addToPlaylist")
    @ResponseBody // Trả về JSON trực tiếp
    public ResponseEntity<Map<String, String>> addSongToPlaylist(@RequestParam Integer playlistId, @RequestParam int songId) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean added = playlistService.addSongToPlaylist(playlistId, songId);
            if (added) {
                response.put("status", "success");
                response.put("message", "Bài hát đã được thêm vào playlist thành công!");
                logger.info("Song ID {} added to Playlist ID {}", songId, playlistId);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Bài hát đã có trong playlist hoặc không tìm thấy.");
                logger.warn("Failed to add Song ID {} to Playlist ID {}: already exists or not found.", songId, playlistId);
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi thêm bài hát vào playlist: " + e.getMessage());
            logger.error("Error adding Song ID {} to Playlist ID {}: {}", songId, playlistId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/api/songs/removeFromPlaylist")
    @ResponseBody // Trả về JSON trực tiếp
    public ResponseEntity<Map<String, String>> removeSongFromPlaylist(@RequestParam Integer playlistId, @RequestParam int songId) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean removed = playlistService.removeSongFromPlaylist(playlistId, songId);
            if (removed) {
                response.put("status", "success");
                response.put("message", "Bài hát đã được xóa khỏi playlist thành công!");
                logger.info("Song ID {} removed from Playlist ID {}", songId, playlistId);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Không thể xóa bài hát khỏi playlist (không tìm thấy hoặc không thuộc playlist).");
                logger.warn("Failed to remove Song ID {} from Playlist ID {}: not found or not in playlist.", songId, playlistId);
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi xóa bài hát khỏi playlist: " + e.getMessage());
            logger.error("Error removing Song ID {} from Playlist ID {}: {}", songId, playlistId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // API để lấy danh sách bài hát hiện tại trong playlist (cho việc cập nhật UI động)
    @GetMapping("/api/{playlistId}/songs")
    @ResponseBody
    public ResponseEntity<Set<Song>> getSongsInPlaylist(@PathVariable Integer playlistId) {
        Set<Song> songs = playlistService.getSongsForPlaylist(playlistId);
        if (!songs.isEmpty() || playlistService.getPlaylistById(playlistId).isPresent()) {
            return ResponseEntity.ok(songs); // Trả về Set<Song> đã được chuyển đổi
        }
        logger.warn("Attempted to retrieve songs for non-existent playlist ID: {}", playlistId);
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public String showPlaylistDetail(@PathVariable("id") Integer id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        // Tải thông tin cơ bản của playlist
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid playlist Id:" + id));

        // Tải tất cả các bài hát với đầy đủ thông tin (album, artists)
        Set<Song> songs = songRepository.findSongsByPlaylistIdWithAlbumAndArtist(id);

        model.addAttribute("playlist", playlist);
        model.addAttribute("songs", songs); // Truyền danh sách bài hát đã được tải đầy đủ sang view
        model.addAttribute("isUserOwner", principal.getName().equals(playlist.getUser().getUsername()));

        return "playlist-detail";
    }

}