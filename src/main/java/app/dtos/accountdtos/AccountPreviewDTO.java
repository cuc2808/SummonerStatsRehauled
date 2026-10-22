package app.dtos.accountdtos;

public record AccountPreviewDTO(
        String puuid,
        String gameName,
        String tagLine,
        String region,
        int profileIconId,
        String rank
) {
}