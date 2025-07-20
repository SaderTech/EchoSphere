package music.echospere.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import music.echospere.entity.User;
import music.echospere.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class ProfileController {
    private UserRepository userRepository;
    @GetMapping("/profile")
    public String showProfileForm(Principal principal, Model model) {
        if (principal == null) {
            model.addAttribute("error", "Bạn cần đăng nhập để xem trang cá nhân.");
            return "profile";
        }
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng: " + username));
        model.addAttribute("user", currentUser);
        return "profile";
    }

    @PostMapping("/profile/edit")
    public String handleEditProfileForm(Principal principal,
                                        User updatedUser,//FIX xu ly tung cai bang requerstParam
                                        Model model) {
        if (principal == null) {
            model.addAttribute("error", "Bạn cần đăng nhập để chỉnh sửa trang cá nhân.");
            return "profile";
        }
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng: " + username));

        // Update user details
        currentUser.setFirstName(updatedUser.getFirstName());
        currentUser.setLastName(updatedUser.getLastName());
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setAvatarUrl(updatedUser.getAvatarUrl() != null ? updatedUser.getAvatarUrl() : currentUser.getAvatarUrl());
        userRepository.save(currentUser);

        model.addAttribute("user", currentUser);
        model.addAttribute("message", "Thông tin cá nhân đã được cập nhật thành công!");
        return "profile";
    }
}
