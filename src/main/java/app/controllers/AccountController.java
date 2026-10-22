package app.controllers;

import app.client.RiotApiClient;
import app.daos.AccountEntriesDAO;
import app.daos.AccountIdentityDAO;
import app.daos.AccountStateDAO;
import app.dtos.accountdtos.*;
import app.entities.account.AccountEntries;
import app.entities.account.AccountIdentity;
import app.entities.account.AccountState;
import app.mappers.AccountMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AccountController implements IAccountController {
    private static final RiotApiClient riotApiClient = new RiotApiClient();
    private static final AccountMapper accountMapper = new AccountMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private AccountIdentityDAO accountIdentityDAO = new AccountIdentityDAO();
    private AccountStateDAO accountStateDAO = new AccountStateDAO();
    private AccountEntriesDAO accountEntriesDAO = new AccountEntriesDAO();



    @Override
    public void addAccount(Context ctx) {
        AccountSearchDTO searchDTO = ctx.bodyAsClass(AccountSearchDTO.class);

        String gameName = searchDTO.gameName();
        String tagLine = searchDTO.tagLine();
        String region = searchDTO.region();

        AccountIdentityDTO identityDTO = riotApiClient.getRiotAccountFromGameNameTaglineAndRegion(riotApiClient.getContinentFromRegion(region), gameName, tagLine);
        identityDTO.setRegion(region);

        String puuid = identityDTO.getPuuid();
        AccountStateDTO stateDTO = riotApiClient.getAccountState(puuid, region);
        AccountEntriesDTO[] entriesDTOs = riotApiClient.getAccountEntries(puuid, region);

        AccountIdentity identity = accountMapper.toEntity(identityDTO);
        AccountState state = accountMapper.toEntity(stateDTO);
        AccountEntries[] entries = accountMapper.toEntity(entriesDTOs);

        accountIdentityDAO.save(identity);
        accountStateDAO.save(state);
        for (AccountEntries entry : entries) {
            accountEntriesDAO.save(entry);
        }

        //Hibernate finds id's
        AccountOverviewDTO accountOverviewDTO = new AccountOverviewDTO(identity, entries, state);

        System.out.println("DB - ACCOUNT ADDED : " + gameName + "#" + tagLine + ", REGION " + region);
        ctx.status(200).json(accountOverviewDTO);
    }

    @Override
    public void findAccount(Context ctx) {
        AccountSearchDTO searchDTO = ctx.bodyAsClass(AccountSearchDTO.class);

        String gameName = searchDTO.gameName();
        String tagLine = searchDTO.tagLine();
        String region = searchDTO.region();

        AccountIdentity accountIdentity = accountIdentityDAO.getByGameNameTagLineAndRegion(gameName, tagLine, region);
        AccountState accountState;
        Set<AccountEntries> accountEntries;

        if(accountIdentity != null) {
            accountEntries = accountEntriesDAO.getByPuuid(accountIdentity.getPuuid());
            accountState = accountStateDAO.getByPuuid(accountIdentity.getPuuid());
            AccountEntries[] accountEntriesArray = accountEntries.toArray(AccountEntries[]::new);
            AccountOverviewDTO accountOverviewDTO = new AccountOverviewDTO(accountIdentity, accountEntriesArray, accountState);

            System.out.println("DB - ACCOUNT FOUND : " + gameName + "#" + tagLine + ", REGION " + region);
            ctx.status(200).json(accountOverviewDTO);
        } else {
            addAccount(ctx);
        }
    }

    @Override
    public void updateAccount(Context ctx) {
        AccountPreviewDTO previewDTO = ctx.bodyAsClass(AccountPreviewDTO.class);

        String gameName;
        String tagLine;
        String region;

        if (previewDTO != null) {
            gameName = previewDTO.gameName();
            tagLine = previewDTO.tagLine();
            region = previewDTO.region();
        } else {
            System.out.println("DB - NO ACCOUNT OR NOTHING TO COMPUTE");
            return;
        }

        AccountIdentityDTO identityDTO = riotApiClient.getRiotAccountFromGameNameTaglineAndRegion(riotApiClient.getContinentFromRegion(region), gameName, tagLine);
        identityDTO.setRegion(region);
        String puuid = identityDTO.getPuuid();

        AccountStateDTO stateDTO = riotApiClient.getAccountState(puuid, region);
        AccountEntriesDTO[] entriesDTOs = riotApiClient.getAccountEntries(puuid, region);

        AccountIdentity identity = accountMapper.toEntity(identityDTO);
        AccountState state = accountMapper.toEntity(stateDTO);
        AccountEntries[] entries = accountMapper.toEntity(entriesDTOs);

        AccountIdentity existingIdentity = accountIdentityDAO.getByPuuid(identity.getPuuid());
        AccountState existingState = accountStateDAO.getByPuuid(state.getPuuid());
        Set<AccountEntries> existingEntries = accountEntriesDAO.getByPuuid(identity.getPuuid());

        identity.setId(existingIdentity.getId());
        state.setId(existingState.getId());
        for (AccountEntries existingEntry : existingEntries) {
            for (AccountEntries entry : entries) {
                if(existingEntry.getQueueType().equals(entry.getQueueType())) {
                    entry.setId(existingEntry.getId());
                }
            }
        }

        accountIdentityDAO.update(identity);
        accountStateDAO.update(state);
        for (AccountEntries entry : entries) {
            accountEntriesDAO.update(entry);
        }

        AccountOverviewDTO accountOverviewDTO = new AccountOverviewDTO(identity, entries, state);

        System.out.println("DB - ACCOUNT UPDATE : " + gameName + "#" + tagLine + ", REGION " + region);
        ctx.status(200).json(accountOverviewDTO);
    }

    @Override
    public void trackAccount(Context ctx) {

    }

    @Override
    public void stopTrackingAccount(Context ctx) {

    }
}
