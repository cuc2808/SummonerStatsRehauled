package app.service;

import app.dtos.accountdtos.AccountStatisticsDTO;
import app.entities.account.AccountEntries;


public class AccountService {

    public AccountStatisticsDTO OverallStatisticsCalculator (AccountEntries[] entries) {
        if (entries == null || entries.length == 0) {
            return new AccountStatisticsDTO(0, 0, 0, 0, 0);
        }

        double soloDuoWinrate = 0;
        double flexWinrate = 0;
        double ranked5Winrate = 0;
        double normalWinrate = 0;

        int totalWins = 0;
        int totalLosses = 0;

        for (AccountEntries entry : entries) {
            int wins = entry.getWins();
            int losses = entry.getLosses();

            double winrate = calculateWinrate (wins, losses);

            System.out.println(entry.getQueueType());
            switch (entry.getQueueType()) {
                case "RANKED_SOLO_5x5":
                    soloDuoWinrate = winrate;
                    break;
                case "RANKED_FLEX_SR":
                    flexWinrate = winrate;
                    break;
                case "RANKED_5x5":
                    ranked5Winrate = winrate;
                    break;
                case "NORMAL":
                    normalWinrate = winrate;
                    break;
            }

            totalWins += wins;
            totalLosses += losses;
        }

        double overallWinrate = calculateWinrate(totalWins, totalLosses);

        return new AccountStatisticsDTO(
                soloDuoWinrate,
                flexWinrate,
                ranked5Winrate,
                normalWinrate,
                overallWinrate
        );
    }

    private double calculateWinrate(int wins, int losses) {
        int totalGames = wins + losses;

        if (totalGames == 0) {
            return 0.0;
        }

        return (double) wins / totalGames * 100;
    }
}
