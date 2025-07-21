package music.echospere.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "songs")
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
    private Set<SongArtist> artists;
}