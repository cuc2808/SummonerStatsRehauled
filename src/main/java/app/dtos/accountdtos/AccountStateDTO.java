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
public class AccountStateDTO {
    @JsonProperty("puuid")
    String puuid;
    @JsonProperty("profileIconId")
    int profileIconId;
    @JsonProperty("summonerLevel")
    long summonerLevel;
    @JsonProperty("revisionDate")
    long revisionDate;
}
