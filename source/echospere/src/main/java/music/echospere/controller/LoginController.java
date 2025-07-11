package music.echospere.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {
    public
    @PostMapping
    public String Login(@RequestParam String username,
                        @RequestParam String password){
        if (username.equals()
    }
}
