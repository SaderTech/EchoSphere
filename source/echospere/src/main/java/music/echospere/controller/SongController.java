package music.echospere.controller;

import music.echospere.entity.Song;
import music.echospere.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/songs")
public class SongController {
    @Autowired
    private SongService songService;

    @GetMapping("/play/{id}")
    public String playSong  (Model model, @PathVariable("id") int id) {
        // Lấy thông tin bài hát từ service
        Optional<Song> songOptional = songService.getSongById(id);

        if (songOptional.isPresent()) {
            model.addAttribute("song", songOptional.get());
            return "fragments/playerbar"; // Trả về view playSong.html
        } else {
            return "redirect:login/home"; // Nếu không tìm thấy bài hát, chuyển hướng về danh sách bài hát
        }
    }
}
