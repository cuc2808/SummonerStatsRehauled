package app.dtos.matchdtos;

public record MatchPreviewDTO(
        String matchId,
        String region,
        String championId,
        boolean win,
        int kills,
        int deaths,
        int assists,
        long gameDuration,
        long gameCreation
) {}