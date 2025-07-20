package music.echospere.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import music.echospere.entity.User;
import music.echospere.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;


@Controller
@AllArgsConstructor
public class ProfileController {
    private final UserRepository userRepository;
    // CẬP NHẬT: Thay đổi đường dẫn lưu file sang thư mục 'uploads' ở gốc dự án
    private static final String UPLOAD_DIR = "uploads/avatars/";
    private static final String WEB_AVATAR_PATH = "/avatars/";

    @GetMapping("/profile")
    public String showProfileForm(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/edit")
    public String handleEditProfileForm(Principal principal,
                                        @RequestParam("firstName") String firstName,
                                        @RequestParam("lastName") String lastName,
                                        @RequestParam("email") String email,
                                        @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
                                        RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user"));

        user.setFirstName(firstName);
        user.setLastName(lastName);

        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String originalFilename = avatarFile.getOriginalFilename();
                String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;

                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(avatarFile.getInputStream(), filePath);

                user.setAvatarUrl(WEB_AVATAR_PATH + uniqueFileName);

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Lỗi khi tải lên avatar.");
                return "redirect:/profile";
            }
        }

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "Hồ sơ đã được cập nhật thành công.");

        return "redirect:/profile";
    }
}