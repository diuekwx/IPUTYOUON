package spotify.recommender.DTO;

public class SpotifyDTO {

    private String artistNames;
    private String description;
    private String songName;
    private String userID;

    public SpotifyDTO() {
    }
    public SpotifyDTO(String artistNames, String songName){
        this.songName = songName;
        this.artistNames = artistNames;
    }

    public String getArtist(){
        return artistNames;
    }
    public void setArtist(String artistNames){
        this.artistNames = artistNames;
    }

    public String getSongName(){
        return songName;
    }
    public void setSongName(String name){
        this.songName= name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
