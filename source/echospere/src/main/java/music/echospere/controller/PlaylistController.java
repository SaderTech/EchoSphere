package music.echospere.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import music.echospere.DTO.PlaylistFormDTO;
import music.echospere.entity.Playlist;
import music.echospere.entity.User;
import music.echospere.repository.PlaylistRepository;
import music.echospere.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class PlaylistController {
    private static final Logger logger = LoggerFactory.getLogger(PlaylistController.class);
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    @GetMapping("/playlists")
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

    @GetMapping("/playlists/add")
    public String showAddPlaylistForm(Model model){
        PlaylistFormDTO playlistFormDTO = new PlaylistFormDTO();
        model.addAttribute("playlistForm", playlistFormDTO);
        return "createPlaylits";
    }

    @PostMapping("/playlists/add")
    public String addPlaylist(@Valid PlaylistFormDTO playlistFormDTO,
                              BindingResult bindingResult,
                              @RequestParam("coverImage") MultipartFile coverImage,
                              Principal principal,
                              Model model){
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors: {}", bindingResult.getAllErrors());
            return "createPlaylits";
        }

        if(principal == null){
            model.addAttribute("errorMessage", "Bạn cần đăng nhập để tạo playlist");
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
            model.addAttribute("successMessage", "Playlist đã được tạo thành công");
            logger.info("Playlist created successfully: {}", playlist.getName());
            return "redirect:/home?filter=playlists";
        }catch (Exception e){
            model.addAttribute("errorMessage", "Lỗi khi tạo playlist: " + e.getMessage());
            logger.error("Error when creating playlist: {}", e.getMessage());
            return "createPlaylits";
        }
    }

    private String saveImage(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File hình ảnh không được rỗng");
        }

        // Định nghĩa thư mục lưu trữ
        String uploadDir = "static/playlistImage/";
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

        // Trả về URL
        return "/playlistImage/" + fileName;
    }
}
