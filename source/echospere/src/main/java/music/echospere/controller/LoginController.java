package music.echospere.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import music.echospere.entity.User;
import music.echospere.repository.UserRepository;
import music.echospere.service.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;


@Controller
@RequestMapping("/login")
@AllArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final UserRepository userRepository;

    @GetMapping
    public String showLoginForm() {
        return "fragments/login";
    }
    @PostMapping
    public String Login(@RequestParam String username,
                        @RequestParam String password){
       loginService.checkLogin(username, password);
       return "redirect:/home";
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
