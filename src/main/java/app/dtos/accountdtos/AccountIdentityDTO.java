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
public class AccountIdentityDTO {
    @JsonProperty("puuid")
    String puuid;
    @JsonProperty("gameName")
    String gameName;
    @JsonProperty("tagLine")
    String tagLine;

    String region;
}