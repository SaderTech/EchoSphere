package music.echospere.controller; // Để lấy thông tin người dùng hiện tại

import music.echospere.entity.User;
import music.echospere.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal; // Để lấy thông tin người dùng đang đăng nhập

@ControllerAdvice // Annotation này giúp Controller này chạy cho TẤT CẢ các request
public class HeaderController {

    @Autowired
    private UserService userService; // Dịch vụ để lấy thông tin người dùng

    /**
     * Phương thức này sẽ được thực thi trước mỗi request đến bất kỳ controller nào.
     * Nó thêm các thuộc tính cần thiết cho header vào Model.
     *
     * @param model Đối tượng Model để thêm thuộc tính.
     * @param principal Thông tin về người dùng đang đăng nhập (nếu có).
     * @param query Tham số tìm kiếm từ URL (nếu có).
     */
    @ModelAttribute
    public void addHeaderAttributes(Model model, Principal principal,
                                    @RequestParam(value = "query", required = false) String query) {

        // Thêm thông tin người dùng hiện tại (cho phần "Tài khoản", "Đăng nhập")
        if (principal != null) {
            String username = principal.getName();
            User currentUser = userService.findByUsername(username); // Giả sử có phương thức này
            model.addAttribute("currentUser", currentUser);
        } else {
            model.addAttribute("currentUser", null); // Hoặc một đối tượng User rỗng
        }

        // Thêm từ khóa tìm kiếm vào model để hiển thị lại trên ô input
        model.addAttribute("query", query);

        // Bạn có thể thêm các thuộc tính khác cần thiết cho header ở đây
        // Ví dụ: thông báo, số lượng mục trong giỏ hàng (nếu có)
    }

    // KHÔNG NÊN ĐẶT CÁC ENDPOINT @GetMapping TRỰC TIẾP VÀO ĐÂY NẾU CHỈ DÙNG @ControllerAdvice
    // @ControllerAdvice chủ yếu để thêmModelAttribute hoặc xử lý ngoại lệ toàn cục.
    // Logic tìm kiếm chính sẽ vẫn nằm trong HomeController của bạn.
}