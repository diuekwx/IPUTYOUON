package spotify.recommender.Entities;

import jakarta.persistence.*;

import java.beans.ConstructorProperties;

@Entity
public class Playlist {
    @Id
    @GeneratedValue
    private long id;

    @Column(name="spotify_playlist_id")
    private String spotifyPlaylistId;

    @ManyToOne
    @JoinColumn(name = "user_owner", referencedColumnName = "spotify_id")
    private Users userOwner;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "playlist_name", columnDefinition = "TEXT")
    private String playlistName;

    @Column(name = "row_index", insertable = false)
    private int rowIndex;

    public Playlist() {
    }

    public Playlist(long id, String spotifyPlaylistId, Users userOwner, String description, String playlistName, int rowIndex) {
        this.id = id;
        this.spotifyPlaylistId = spotifyPlaylistId;
        this.userOwner = userOwner;
        this.description = description;
        this.playlistName = playlistName;
        this.rowIndex = rowIndex;
    }


    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSpotifyPlaylistId() {
        return spotifyPlaylistId;
    }

    public void setSpotifyPlaylistId(String spotifyPlaylistId) {
        this.spotifyPlaylistId = spotifyPlaylistId;
    }

    public Users getUserOwner() {
        return userOwner;
    }

    public void setUserOwner(Users userOwner) {
        this.userOwner = userOwner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
