package app.dtos.accountdtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountEntriesDTO {
    @JsonProperty("puuid")
    String puuid;
    @JsonProperty("queueType")
    String queueType;
    @JsonProperty("tier")
    String tier;
    @JsonProperty("rank")
    String rank;
    @JsonProperty("leaguePoints")
    int leaguePoints;
    @JsonProperty("wins")
    int wins;
    @JsonProperty("losses")
    int losses;
    @JsonProperty("hotStreak")
    boolean hotStreak;
    @JsonProperty("veteran")
    boolean veteran;
    @JsonProperty("freshBlood")
    boolean freshBlood;
    @JsonProperty("inactive")
    boolean inactive;

}
