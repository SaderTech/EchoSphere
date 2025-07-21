package music.echospere.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class PlaylistFormDTO {
    // Getters và Setters
    private Integer id; // Khớp với IDENTITY trong cơ sở dữ liệu

    @NotBlank(message = "Tên playlist không được để trống")
    private String namePlaylist;

    private String playlistUrl;

    private boolean isPublic = true; // Mặc định công khai, khớp với is_public BIT DEFAULT 1

}