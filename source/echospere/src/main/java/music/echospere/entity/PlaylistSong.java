package music.echospere.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "playlist_songs")
public class PlaylistSong {
    @EmbeddedId
    private PlaylistSongId id;

    @MapsId("playlistId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @ColumnDefault("getdate()")
    @Column(name = "date_added")
    private Instant dateAdded;

}