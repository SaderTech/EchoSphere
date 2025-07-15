package music.echospere.controller;

import music.echospere.entity.User;
import music.echospere.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Controller
public class ChangePassController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePassController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        return "changePassword";
    }

    @PostMapping("/change-password")
    public String handleChangePasswordForm(Principal principal,
                                           @RequestParam("currentPassword") String currentPassword,
                                           @RequestParam("newPassword") String newPassword,
                                           @RequestParam("confirmNewPassword") String confirmPassword,
                                           Model model) {
        if (principal == null) {
            model.addAttribute("error", "Bạn cần đăng nhập để đổi mật khẩu.");
            return "changePassword";
        }
        String username = principal.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy người dùng.");
            return "changePassword";
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            model.addAttribute("error", "Mật khẩu hiện tại không đúng!");
            return "changePassword";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp!");
            return "changePassword";
        }
        if (newPassword.length() < 8 || newPassword.length() > 20) {
            model.addAttribute("error", "Mật khẩu mới phải có độ dài từ 8 đến 20 ký tự!");
            return "changePassword";
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "redirect:/login?passwordChanged";
    }
}
