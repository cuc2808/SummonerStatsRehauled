package app.dtos.matchdtos;

import java.util.List;

public record MatchHistoryDTO (
    String[] matchIds,
    List<MatchPreviewDTO> matches
)
{}
