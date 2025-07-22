package music.echospere.controller;

import music.echospere.entity.User;
import music.echospere.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal; // Để lấy thông tin người dùng đang đăng nhập
import java.util.Optional; // Cần import Optional

@ControllerAdvice
public class HeaderController {

    @Autowired
    private UserService userService; // Đảm bảo đã inject UserService

    @ModelAttribute
    public void addHeaderAttributes(Model model, Principal principal,
                                    @RequestParam(value = "query", required = false) String query) {

        if (principal != null) {
            String username = principal.getName();
            // Lấy Optional<User> từ UserService
            Optional<User> userOptional = userService.findByUsername(username);

            // Giải nén Optional:
            // Cách 1: Sử dụng orElse(null) nếu bạn chấp nhận currentUser có thể là null
            User currentUser = userOptional.orElse(null);

            // Hoặc Cách 2: Sử dụng orElseGet để tạo một đối tượng User mặc định (ví dụ: User cho khách)
            // User currentUser = userOptional.orElseGet(() -> new User("Guest", "guest@example.com"));

            // Hoặc Cách 3: Sử dụng ifPresent để chỉ thêm vào model nếu user tồn tại
            // userOptional.ifPresent(user -> model.addAttribute("currentUser", user));
            // if (!userOptional.isPresent()) { model.addAttribute("currentUser", null); }

            model.addAttribute("currentUser", currentUser);
        } else {
            // Nếu không có người dùng nào đăng nhập, currentUser là null
            model.addAttribute("currentUser", null);
        }

        model.addAttribute("query", query);
    }
}