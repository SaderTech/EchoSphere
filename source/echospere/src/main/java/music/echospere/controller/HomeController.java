package music.echospere.controller;

import jakarta.servlet.http.HttpSession;
import music.echospere.entity.User;
import music.echospere.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class HomeController {
    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/home")
    public String home(Principal principal, HttpSession session) {
        if (principal != null) {
            Optional<User> userOpt = userRepository.findByUsername(principal.getName());
            userOpt.ifPresent(user -> session.setAttribute("userID", user.getId()));
        } else {
            session.removeAttribute("userID");
        }
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session,User user, Model model) {
        if (session.getAttribute("userID") != null) {
            session.removeAttribute("userID");
            model.addAttribute("message", "Bạn đã đăng xuất thành công.");
        } else {
            model.addAttribute("message", "Bạn chưa đăng nhập.");
        }
        return "home";
    }
}
