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
public class SongGenreId implements Serializable {
    private static final long serialVersionUID = -2818008556023052144L;
    @Column(name = "song_id", nullable = false)
    private Integer songId;

    @Column(name = "genre_id", nullable = false)
    private Integer genreId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SongGenreId entity = (SongGenreId) o;
        return Objects.equals(this.genreId, entity.genreId) &&
                Objects.equals(this.songId, entity.songId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genreId, songId);
    }

}