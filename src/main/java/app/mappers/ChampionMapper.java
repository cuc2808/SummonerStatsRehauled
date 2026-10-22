package app.mappers;

import app.dtos.championdtos.ChampionDTO;
import app.entities.Champion;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;

public class ChampionMapper {
    Champion toEntity(ChampionDTO dto) {
        return new Champion(
                dto.getId(),
                dto.getKey(),
                dto.getName(),
                dto.getTitle()
        );
    }
}
