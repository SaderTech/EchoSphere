package music.echospere.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Lấy đường dẫn tuyệt đối đến thư mục gốc của dự án
        Path uploadDir = Paths.get("./uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // Cấu hình resource handler để phục vụ các file từ thư mục 'uploads'
        // Khi có request tới /avatars/**, Spring sẽ tìm file trong thư mục 'file:/path/to/project/uploads/'
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:/" + uploadPath + "/avatars/");
    }
}