package music.echospere.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import music.echospere.repository.UserRepository;
import music.echospere.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/login")
@AllArgsConstructor
public class LoginController {
    @Autowired
    private static LoginService loginService;
    private final UserRepository userRepository;
    @PostMapping
    public String Login(@RequestParam String username,
                        @RequestParam String password){
       loginService.checkLogin(username, password);
    }

}
