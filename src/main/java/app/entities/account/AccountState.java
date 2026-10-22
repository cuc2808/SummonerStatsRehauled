package app.entities.account;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountState {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String puuid;

    private int profileIconId;
    private long summonerLevel;
    private long revisionDate;

    public AccountState(String puuid, int profileIconId, long summonerLevel, long revisionDate) {
        this.puuid = puuid;
        this.profileIconId = profileIconId;
        this.summonerLevel = summonerLevel;
        this.revisionDate = revisionDate;
    }
}
