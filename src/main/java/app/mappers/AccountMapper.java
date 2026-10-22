package app.mappers;

import app.dtos.accountdtos.AccountEntriesDTO;
import app.dtos.accountdtos.AccountIdentityDTO;
import app.dtos.accountdtos.AccountStateDTO;
import app.entities.account.AccountEntries;
import app.entities.account.AccountIdentity;
import app.entities.account.AccountState;


public class AccountMapper {
    public AccountIdentity toEntity(AccountIdentityDTO dto) {
        return new AccountIdentity(dto.getPuuid(), dto.getGameName(), dto.getTagLine(), dto.getRegion());
    }

    public AccountState toEntity(AccountStateDTO dto) {
        return new AccountState(dto.getPuuid(), dto.getProfileIconId(), dto.getSummonerLevel(), dto.getRevisionDate());
    }

    public AccountEntries[] toEntity(AccountEntriesDTO[] accountEntriesDTOList) {
        AccountEntries[] accountEntries = new AccountEntries[accountEntriesDTOList.length];

        for (int i = 0; i < accountEntriesDTOList.length; i++) {
            AccountEntriesDTO dto = accountEntriesDTOList[i];

            accountEntries[i] = new AccountEntries(
                    dto.getPuuid(),
                    dto.getQueueType(),
                    dto.getTier(),
                    dto.getRank(),
                    dto.getLeaguePoints(),
                    dto.getWins(),
                    dto.getLosses(),
                    dto.isHotStreak(),
                    dto.isVeteran(),
                    dto.isFreshBlood(),
                    dto.isInactive()
            );
        }

        return accountEntries;
    }
}
