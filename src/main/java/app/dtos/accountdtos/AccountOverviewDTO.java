package app.dtos.accountdtos;

import app.entities.MatchPreview;
import app.entities.account.AccountEntries;
import app.entities.account.AccountIdentity;
import app.entities.account.AccountState;

import java.util.Set;

public record AccountOverviewDTO(
        AccountIdentity accountIdentity,
        AccountEntries[] accountEntries,
        AccountState accountState,
        String[] matchHistoryIds,
        Set<MatchPreview> matchHistory,
        AccountStatisticsDTO accountStatisticsDTO
) {}