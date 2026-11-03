package app.controllers;

import io.javalin.http.HttpStatus;
import app.client.RiotApiClient;
import app.daos.AccountEntriesDAO;
import app.daos.AccountIdentityDAO;
import app.daos.AccountStateDAO;
import app.daos.MatchPreviewDAO;
import app.dtos.accountdtos.*;
import app.dtos.matchdtos.MatchDTO;
import app.entities.MatchPreview;
import app.entities.account.AccountEntries;
import app.entities.account.AccountIdentity;
import app.entities.account.AccountState;
import app.mappers.AccountMapper;
import app.mappers.MatchMapper;
import app.service.AccountService;
import app.exceptions.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AccountController implements IAccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private static final RiotApiClient riotApiClient = new RiotApiClient();
    private static final AccountMapper accountMapper = new AccountMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MatchMapper matchMapper = new MatchMapper();
    private MatchPreviewDAO matchPreviewDAO = new MatchPreviewDAO();
    private AccountIdentityDAO accountIdentityDAO = new AccountIdentityDAO();
    private AccountStateDAO accountStateDAO = new AccountStateDAO();
    private AccountEntriesDAO accountEntriesDAO = new AccountEntriesDAO();
    private AccountService accountService = new AccountService();
    private static final Set<String> SUPPORTED_REGIONS = Set.of(
            "br1", "eun1", "euw1", "jp1", "kr", "la1", "la2", "na1",
            "oc1", "tr1", "ru", "ph2", "sg2", "th2", "tw2", "vn2", "me1");

    @Override
    public void addAccount(Context ctx, AccountSearchDTO searchDTO) {
        searchDTO = validateSearch(searchDTO);
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

        String[] matchIds = riotApiClient.getMatchHistory(riotApiClient.getContinentFromRegion(region), puuid, "20");
        List<MatchDTO> matchHistory = riotApiClient.getMatchHistoryInMatchDTOs(matchIds, riotApiClient.getContinentFromRegion(region));
        Set<MatchPreview> matchPreviewList = new HashSet<>();
        for (MatchDTO match : matchHistory) {
            MatchPreview preview = matchMapper.toEntity(match, puuid);
            preview.setRegion(region);
            matchPreviewDAO.save(preview);
            matchPreviewList.add(preview);
        }

        accountIdentityDAO.save(identity);
        accountStateDAO.save(state);
        for (AccountEntries entry : entries) {
            accountEntriesDAO.save(entry);
        }

        AccountStatisticsDTO accountStatisticsDTO = accountService.OverallStatisticsCalculator(entries);

        //Hibernate finds id's
        AccountOverviewDTO accountOverviewDTO = new AccountOverviewDTO(identity, entries, state, matchIds, matchPreviewList, accountStatisticsDTO);

       logger.info("DB - ACCOUNT ADDED : {}#{}, REGION {}", gameName, tagLine, region);
        ctx.status(HttpStatus.OK).json(accountOverviewDTO);
    }

    @Override
    public void findAccount(Context ctx) {
        AccountSearchDTO searchDTO = validateSearch(new AccountSearchDTO(
                ctx.queryParam("gameName"), ctx.queryParam("tagLine"), ctx.queryParam("region")));
        String region = searchDTO.region();
        String gameName = searchDTO.gameName();
        String tagLine = searchDTO.tagLine();

        AccountIdentity accountIdentity = accountIdentityDAO.getByGameNameTagLineAndRegion(gameName, tagLine, region);
        AccountState accountState;
        Set<AccountEntries> accountEntries;

        if(accountIdentity != null) {
            accountEntries = accountEntriesDAO.getByPuuid(accountIdentity.getPuuid());
            accountState = accountStateDAO.getByPuuid(accountIdentity.getPuuid());
            AccountEntries[] accountEntriesArray = accountEntries.toArray(AccountEntries[]::new);
            Set<MatchPreview> matchPreviewList = matchPreviewDAO.getByPuuid(accountIdentity.getPuuid());
            String[] matchIds = matchPreviewList.stream().map(MatchPreview::getMatchId).toArray(String[]::new);

            AccountStatisticsDTO accountStatisticsDTO = accountService.OverallStatisticsCalculator(accountEntriesArray);

            AccountOverviewDTO accountOverviewDTO = new AccountOverviewDTO(accountIdentity, accountEntriesArray, accountState, matchIds, matchPreviewList, accountStatisticsDTO);

            logger.info("DB - ACCOUNT FOUND : " + gameName + "#" + tagLine + ", REGION " + region);
            ctx.status(HttpStatus.OK).json(accountOverviewDTO);
        } else {
            addAccount(ctx, searchDTO);
        }
    }

    @Override
    public void updateAccount(Context ctx) {
        AccountPreviewDTO previewDTO;
        try {
            previewDTO = objectMapper.readValue(ctx.body(), AccountPreviewDTO.class);
        } catch (JsonProcessingException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Request body must be a valid JSON object.");
        }
        if (previewDTO == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Request body must be a valid JSON object.");
        }
        AccountSearchDTO searchDTO = validateSearch(new AccountSearchDTO(
                previewDTO.gameName(), previewDTO.tagLine(), previewDTO.region()));
        String gameName = searchDTO.gameName();
        String tagLine = searchDTO.tagLine();
        String region = searchDTO.region();

        AccountIdentityDTO identityDTO = riotApiClient.getRiotAccountFromGameNameTaglineAndRegion(riotApiClient.getContinentFromRegion(region), gameName, tagLine);
        identityDTO.setRegion(region);
        String puuid = identityDTO.getPuuid();

        AccountIdentity existingIdentity = accountIdentityDAO.getByPuuid(puuid);
        AccountState existingState = accountStateDAO.getByPuuid(puuid);
        if (existingIdentity == null || existingState == null) {
            throw new ApiException(HttpStatus.NOT_FOUND,
                    "Account has not been added yet. Find the account before updating it.");
        }

        AccountStateDTO stateDTO = riotApiClient.getAccountState(puuid, region);
        AccountEntriesDTO[] entriesDTOs = riotApiClient.getAccountEntries(puuid, region);

        AccountIdentity identity = accountMapper.toEntity(identityDTO);
        AccountState state = accountMapper.toEntity(stateDTO);
        AccountEntries[] entries = accountMapper.toEntity(entriesDTOs);

        String[] matchIds = riotApiClient.getMatchHistory(riotApiClient.getContinentFromRegion(region), puuid, "20");
        List<MatchDTO> matchHistory = riotApiClient.getMatchHistoryInMatchDTOs(matchIds, riotApiClient.getContinentFromRegion(region));


        Set<AccountEntries> existingEntries = accountEntriesDAO.getByPuuid(identity.getPuuid());
        matchPreviewDAO.deleteByPuuid(identity.getPuuid());


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
        Set<MatchPreview> matchPreviewList = new HashSet<>();
        for (MatchDTO match : matchHistory) {
            MatchPreview preview = matchMapper.toEntity(match, puuid);
            preview.setRegion(region);
            matchPreviewDAO.save(preview);
            matchPreviewList.add(preview);
        }

        AccountStatisticsDTO accountStatisticsDTO = accountService.OverallStatisticsCalculator(entries);

        AccountOverviewDTO accountOverviewDTO = new AccountOverviewDTO(identity, entries, state, matchIds, matchPreviewList, accountStatisticsDTO);

        logger.info("DB - ACCOUNT UPDATED: {}#{}, REGION {}", gameName, tagLine, region);
        ctx.status(HttpStatus.OK).json(accountOverviewDTO);
    }

    private AccountSearchDTO validateSearch(AccountSearchDTO search) {
        if (search == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Account search is required.");
        }
        String gameName = requireField(search.gameName(), "gameName");
        String tagLine = requireField(search.tagLine(), "tagLine");
        String region = requireField(search.region(), "region").toLowerCase(Locale.ROOT);
        if (!SUPPORTED_REGIONS.contains(region)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported region.");
        }
        return new AccountSearchDTO(gameName, tagLine, region);
    }

    private String requireField(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, field + " is required.");
        }
        return value.strip();
    }

    @Override
    public void trackAccount(Context ctx) {

    }

    @Override
    public void stopTrackingAccount(Context ctx) {

    }
}
