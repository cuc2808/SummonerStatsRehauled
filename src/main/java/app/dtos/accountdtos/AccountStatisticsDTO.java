package app.dtos.accountdtos;

public record AccountStatisticsDTO (
        double soloDuoWinrate,
        double flexWinrate,
        double ranked5Winrate,
        double normalWinrate,
        double overallWinrate
)
{}
