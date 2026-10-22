package app.dtos.matchdtos;

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
public class ObjectivesDTO {
    @JsonProperty("baron")
    ObjectiveDTO baron;
    @JsonProperty("champion")
    ObjectiveDTO champion;
    @JsonProperty("dragon")
    ObjectiveDTO dragon;
    @JsonProperty("horde")
    ObjectiveDTO horde;
    @JsonProperty("inhibitor")
    ObjectiveDTO inhibitor;
    @JsonProperty("riftHerald")
    ObjectiveDTO riftHerald;
    @JsonProperty("tower")
    ObjectiveDTO tower;

}
