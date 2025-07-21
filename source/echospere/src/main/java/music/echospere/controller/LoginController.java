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

    @PostMapping // Phương thức đã sửa đổi
    public String Login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) { // Thêm tham số HttpSession
        // Giả sử checkLogin trả về Optional<User> hoặc null nếu không thành công
        Optional<User> userOpt = loginService.checkLogin(username, password);
        if (userOpt.isPresent()) {
            session.setAttribute("userID", userOpt.get().getId());
            return "redirect:/home";
        } else {
            // Xử lý đăng nhập không hợp lệ (ví dụ: thêm lỗi vào model, quay lại trang đăng nhập)
            // Hiện tại, chúng ta chỉ chuyển hướng về trang đăng nhập hoặc trang lỗi
            return "redirect:/login?error";
        }
    }

    @GetMapping("/home")
    public String home(Principal principal, HttpSession session) {
        // Phương thức này giờ đây có thể tập trung chủ yếu vào việc đặt các thuộc tính model
        // session.userID đã được đặt nếu đăng nhập thành công.
        // Bạn vẫn có thể muốn giữ lại đoạn này để truy cập trực tiếp /home hoặc để xác minh lại.
        if (principal != null && session.getAttribute("userID") == null) { // Chỉ đặt nếu chưa được đặt
            Optional<User> userOpt = userRepository.findByUsername(principal.getName());
            userOpt.ifPresent(user -> session.setAttribute("userID", user.getId()));
        } else if (principal == null) {
            session.removeAttribute("userID"); // Đảm bảo phiên sạch nếu không có Principal
        }
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) { // Đã xóa tham số User user vì không được sử dụng
        if (session.getAttribute("userID") != null) {
            session.removeAttribute("userID");
            model.addAttribute("message", "Bạn đã đăng xuất thành công.");
        } else {
            model.addAttribute("message", "Bạn chưa đăng nhập.");
        }
        return "redirect:/home"; // Chuyển hướng về trang chủ sau khi đăng xuất
    }
}