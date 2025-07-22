package music.echospere.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "songs")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Song {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(name = "song_url", nullable = false)
    private String songUrl;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @OneToMany(mappedBy = "song")
    @JsonIgnore // Quan trọng: Ẩn collection phức tạp này khỏi quá trình chuyển đổi JSON
    private Set<SongArtist> artists;

    // Thêm phương thức này để tạo một thuộc tính "artistsJoined" trong JSON trả về
    // Thuộc tính này chứa tên các nghệ sĩ dưới dạng một chuỗi String đơn giản
    @JsonProperty("artistsJoined")
    public String getArtistsJoined() {
        if (artists == null || artists.isEmpty()) {
            return "Nghệ sĩ chưa xác định";
        }
        return artists.stream()
                .map(songArtist -> songArtist.getArtist().getName())
                .collect(Collectors.joining(", "));
    }
}