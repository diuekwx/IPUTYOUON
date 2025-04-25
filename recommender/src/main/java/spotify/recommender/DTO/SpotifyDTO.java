package spotify.recommender.DTO;

public class SpotifyDTO {

    private String name;
    private String description;
    private String userID;

    public SpotifyDTO() {
    }
    public SpotifyDTO(String name, String desc){
        this.name = name;
        this.description = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
