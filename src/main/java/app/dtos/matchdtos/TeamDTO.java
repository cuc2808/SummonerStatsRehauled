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
public class TeamDTO {
    @JsonProperty("bans")
    List<BanDTO> bans;
    @JsonProperty("objectives")
    ObjectiveDTO objectives;
    @JsonProperty("teamId")
    int teamId;
    @JsonProperty("win")
    boolean win;
}
