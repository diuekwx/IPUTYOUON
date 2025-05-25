package spotify.recommender.DTO;

public class ContributionDTO {
    private String username;
    private String contribution;

    public ContributionDTO(String username, String contribution) {
        this.username = username;
        this.contribution = contribution;
    }

    public String getUsername() {
        return username;
    }

    public String getContribution() {
        return contribution;
    }
}
