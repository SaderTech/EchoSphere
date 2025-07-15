package music.echospere.controller;

import lombok.AllArgsConstructor;
import music.echospere.repository.UserRepository;
import music.echospere.service.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


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

}
