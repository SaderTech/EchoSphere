package music.echospere.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "song_genres")
public class SongGenre {
    @EmbeddedId
    private SongGenreId id;

    @MapsId("genreId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

}