package music.echospere.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import music.echospere.entity.Album;
import music.echospere.entity.Artist;
import music.echospere.entity.Playlist;
import music.echospere.entity.Song;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class filterDTO {
    List<Song> songs;
    List<Album> albums;
    List<Artist> artists;
    List<Playlist> playlists;

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

}
