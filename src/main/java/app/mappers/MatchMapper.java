package app.mappers;

import app.dtos.matchdtos.MatchDTO;
import app.dtos.matchdtos.MatchPreviewDTO;
import app.dtos.matchdtos.ParticipantDTO;
import app.entities.MatchPreview;
import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class MatchMapper {
    public MatchPreview toEntity(MatchDTO dto, String puuid) {
        ParticipantDTO participant = dto.getInfo()
                .getParticipants()
                .stream()
                .filter(p -> p.getPuuid().equals(puuid))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No Player Found"
                ));
        MatchPreview matchPreview = new MatchPreview();
        matchPreview.setMatchId(dto.getMetadata().getMatchId());
        matchPreview.setPuuid(puuid);
        matchPreview.setChampionId(participant.getChampionName());
        matchPreview.setWin(participant.isWin());
        matchPreview.setKills(participant.getKills());
        matchPreview.setDeaths(participant.getDeaths());
        matchPreview.setAssists(participant.getAssists());
        matchPreview.setGameCreation(dto.getInfo().getGameCreation());
        matchPreview.setGameDuration(dto.getInfo().getGameDuration());
        return matchPreview;
    }

    public MatchPreviewDTO toDTO (MatchPreview matchPreview) {
        return new MatchPreviewDTO(
                matchPreview.getMatchId(),
                matchPreview.getRegion(),
                matchPreview.getChampionId(),
                matchPreview.isWin(),
                matchPreview.getKills(),
                matchPreview.getDeaths(),
                matchPreview.getAssists(),
                matchPreview.getGameCreation(),
                matchPreview.getGameDuration()
        );
    }

    public MatchPreviewDTO toDTO(MatchDTO dto, String puuid, String region) {
        ParticipantDTO participant = dto.getInfo()
                .getParticipants()
                .stream()
                .filter(p -> p.getPuuid().equals(puuid))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No Player Found"
                ));
        MatchPreviewDTO matchPreviewDTO = new MatchPreviewDTO(
                dto.getMetadata().getMatchId(),
                region,
                participant.getChampionName(),
                participant.isWin(),
                participant.getKills(),
                participant.getDeaths(),
                participant.getAssists(),
                dto.getInfo().getGameCreation(),
                dto.getInfo().getGameDuration()
                );
        return matchPreviewDTO;
    }
}
