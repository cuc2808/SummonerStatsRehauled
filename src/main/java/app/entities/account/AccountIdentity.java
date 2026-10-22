package app.entities.account;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountIdentity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String puuid;

    private String gameName;
    private String tagLine;
    private String region;

    public AccountIdentity(String puuid, String gameName, String tagLine, String region) {
        this.puuid = puuid;
        this.gameName = gameName;
        this.tagLine = tagLine;
        this.region = region;
    }
}
