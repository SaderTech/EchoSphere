package music.echospere.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


// Xóa chú thích @Getter và @Setter nếu bạn tự định nghĩa tất cả các getter/setter,
// hoặc sử dụng @Accessors(fluent = true) với các trường boolean để có cách đặt tên getter khác.
// Đối với vấn đề cụ thể này, việc tự định nghĩa getter cho isPublic là chìa khóa.
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistFormDTO {
    private Integer id;

    @NotBlank(message = "Tên playlist không được để trống")
    private String namePlaylist;

    private String playlistUrl;

    private boolean isPublic;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNamePlaylist() {
        return namePlaylist;
    }

    public void setNamePlaylist(String namePlaylist) {
        this.namePlaylist = namePlaylist;
    }

    public String getPlaylistUrl() {
        return playlistUrl;
    }

    public void setPlaylistUrl(String playlistUrl) {
        this.playlistUrl = playlistUrl;
    }

    // Định nghĩa rõ ràng getter là getIsPublic()
    public boolean getIsPublic() { // Đã thay đổi từ isPublic()
        return isPublic;
    }

    // Giữ setter là setPublic hoặc đổi tên thành setIsPublic nếu muốn nhất quán
    public void setIsPublic(boolean isPublic) { // Đã thay đổi từ setPublic()
        this.isPublic = isPublic;
    }
}