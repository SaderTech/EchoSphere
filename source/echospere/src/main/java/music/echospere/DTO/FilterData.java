package music.echospere.DTO; // Đảm bảo đúng package của bạn

import java.util.ArrayList;
import java.util.List;

// Import các lớp Entity của bạn (Song, Album, Artist, Playlist)

import music.echospere.entity.Album;
import music.echospere.entity.Artist;
import music.echospere.entity.Playlist;
import music.echospere.entity.Song;

public class FilterData { // Không cần 'static' ở đây nữa
    private List<Song> songs = new ArrayList<>();
    private List<Album> albums = new ArrayList<>();
    private List<Artist> artists = new ArrayList<>();
    private List<Playlist> playlists = new ArrayList<>();

    // Getters and Setters
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