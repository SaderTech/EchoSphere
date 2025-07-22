package music.echospere.controller;

import music.echospere.entity.Song;
import music.echospere.service.SongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SongController {
    private static final Logger logger = LoggerFactory.getLogger(SongController.class);

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/homesong")
    public String home(Model model) {
        Song song = songService.getDefaultSong(); // Thay bằng logic thực tế để lấy bài hát
        if (song == null) {
            logger.warn("Không tìm thấy bài hát cho trang home");
            model.addAttribute("song", null);
        } else {
            logger.info("Bài hát được tải: {}", song.getTitle());
            model.addAttribute("song", song);
        }
        return "home"; // Ánh xạ tới home.html
    }
}