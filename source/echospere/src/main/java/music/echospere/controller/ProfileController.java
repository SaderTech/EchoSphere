package music.echospere.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import music.echospere.entity.User;
import music.echospere.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // <-- Import này cần thiết
import org.springframework.web.multipart.MultipartFile; // <-- Import này cần thiết
import java.io.File; // <-- Import này cần thiết
import java.io.IOException; // <-- Import này cần thiết
import java.nio.file.Files; // <-- Import này cần thiết
import java.nio.file.Path; // <-- Import này cần thiết
import java.nio.file.Paths; // <-- Import này cần thiết
import java.security.Principal;
import java.util.UUID; // <-- Import này cần thiết cho tên file duy nhất


@Controller
@AllArgsConstructor
public class ProfileController {
    private UserRepository userRepository;
    private static final String UPLOAD_DIR = "src/main/resources/static/avatars/";


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
                                        @RequestParam("firstName") String firstName,
                                        @RequestParam("lastName") String lastName,
                                        @RequestParam("email") String email,
                                        @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
                                        Model model) {
        if (principal == null) {
            model.addAttribute("error", "Bạn cần đăng nhập để chỉnh sửa trang cá nhân.");
            return "profile";
        }
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng: " + username));

        // Update user details
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setEmail(email);
// Xử lý upload avatar nếu có file mới
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                // Đảm bảo thư mục lưu trữ tồn tại
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Tạo tên file duy nhất để tránh trùng lặp
                String originalFilename = avatarFile.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String newFileName = UUID.randomUUID().toString() + fileExtension;
                Path filePath = uploadPath.resolve(newFileName);

                // Lưu file vào thư mục
                Files.copy(avatarFile.getInputStream(), filePath);

                // Cập nhật URL avatar cho người dùng
                // Giả sử avatar được truy cập qua /images/avatars/newFileName
                currentUser.setAvatarUrl("/avatars/" + newFileName);

            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "Không thể lưu ảnh đại diện. Vui lòng thử lại.");
                model.addAttribute("user", currentUser); // Giữ lại dữ liệu hiện tại
                return "profile";
            }
        }        userRepository.save(currentUser);

        model.addAttribute("user", currentUser);
        model.addAttribute("message", "Thông tin cá nhân đã được cập nhật thành công!");
        return "profile";
    }
}
