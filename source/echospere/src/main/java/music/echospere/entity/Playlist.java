package music.echospere.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "playlists")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "name", nullable = false)
    private String name;

    @ColumnDefault("1")
    @Column(name = "is_public")
    private Boolean isPublic;

    @ColumnDefault("getdate()")
    @Column(name = "created_date")
    private Instant createdDate;

}