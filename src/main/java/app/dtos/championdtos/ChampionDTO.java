package app.dtos.championdtos;

import app.entities.Champion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChampionDTO {
    @Id
    @JsonProperty("id")
    String id;
    @JsonProperty("key")
    int key;
    @JsonProperty("name")
    String name;
    @JsonProperty("title")
    String title;
}
