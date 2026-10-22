package app.dtos.matchdtos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfoDTO {
    @JsonProperty("endOfGameResult")
    String endOfGameResult;
    @JsonProperty("gameCreation")
    long gameCreation;
    @JsonProperty("gameDuration")
    long gameDuration;
    @JsonProperty("gameEndTimestamp")
    long gameEndTimestamp;
    @JsonProperty("gameId")
    long gameId;
    @JsonProperty("gameMode")
    String gameMode;
    @JsonProperty("gameName")
    String gameName;
    @JsonProperty("gameStartTimestamp")
    long gameStartTimestamp;
    @JsonProperty("gameType")
    String gameType;
    @JsonProperty("gameVersion")
    String gameVersion;
    @JsonProperty("mapId")
    int mapId;
    @JsonProperty("participants")
    List<ParticipantDTO> participants;
    @JsonProperty("platformId")
    String platformId;
    @JsonProperty("queueId")
    int queueId;
    @JsonProperty("teams")
    List<TeamDTO> teams;
    @JsonProperty("tournamentCode")
    String tournamentCode;
}
