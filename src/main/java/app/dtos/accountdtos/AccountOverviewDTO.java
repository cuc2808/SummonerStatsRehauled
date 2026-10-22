package app.dtos.accountdtos;

import app.entities.account.AccountEntries;
import app.entities.account.AccountIdentity;
import app.entities.account.AccountState;

import java.util.List;

public record AccountOverviewDTO(
        AccountIdentity accountIdentity,
        AccountEntries[] accountEntries,
        AccountState accountState
) {}