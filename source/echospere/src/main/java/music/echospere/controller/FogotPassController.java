package music.echospere.controller;

import lombok.AllArgsConstructor;
import music.echospere.service.ForgotService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class FogotPassController {
    private final ForgotService forgotService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgotpassword"; // This should return the forgot password form view
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        boolean success = forgotService.forgotPass(email);

        if (success) {
            model.addAttribute("message", "Mật khẩu mới đã được gửi về email của bạn!");
        } else {
            model.addAttribute("error", "Email không tồn tại trong hệ thống!");
        }
        return "forgotpassword";
    }
}