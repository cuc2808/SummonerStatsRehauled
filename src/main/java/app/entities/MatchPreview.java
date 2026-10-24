package app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MatchPreview {

    @Id
    private String matchId;

    @Column(nullable = false)
    private String puuid;

    private String region;
    private String championId;
    private boolean win;
    private int kills;
    private int deaths;
    private int assists;
    private long gameDuration;
    private long gameCreation;
}