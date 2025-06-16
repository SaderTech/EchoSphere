package music.echospere.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "song_artists")
public class SongArtist {
    @EmbeddedId
    private SongArtistId id;

    @MapsId("artistId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

}