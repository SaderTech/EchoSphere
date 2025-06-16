package music.echospere.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class SongArtistId implements Serializable {
    private static final long serialVersionUID = -2408955688636419370L;
    @Column(name = "song_id", nullable = false)
    private Integer songId;

    @Column(name = "artist_id", nullable = false)
    private Integer artistId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SongArtistId entity = (SongArtistId) o;
        return Objects.equals(this.artistId, entity.artistId) &&
                Objects.equals(this.songId, entity.songId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artistId, songId);
    }

}