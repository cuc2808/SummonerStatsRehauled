package app.client;

import app.dtos.accountdtos.AccountEntriesDTO;
import app.dtos.accountdtos.AccountIdentityDTO;
import app.dtos.accountdtos.AccountStateDTO;
import app.dtos.championdtos.ChampionDTO;
import app.dtos.matchdtos.MatchDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class RiotApiClient {
    // APIKEY
    private static String apiKey = System.getenv("api_key");

    //APIReader instance and ObjectMapper
    private static APIReader apiReader = new APIReader();
    private static ObjectMapper objectMapper = new ObjectMapper();

    // API URLs - Named after operation:
    private static final String URLGetAccountIdentityFromPUUID = "https://REGION.api.riotgames.com/riot/account/v1/accounts/by-puuid/PUUID?api_key=" + apiKey;
    private static final String URLGetRiotAccountFromGameNameTaglineAndRegion = "https://REGION.api.riotgames.com/riot/account/v1/accounts/by-riot-id/GAMENAME/TAGLINE?api_key=" + apiKey;
    private static final String URLGetAccountState = "https://REGION.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/PUUID?api_key=" + apiKey;
    private static final String URLGetAccountEntries = "https://REGION.api.riotgames.com/lol/league/v4/entries/by-puuid/PUUID?api_key=" + apiKey;
    private static final String matchHistoryURL = "https://REGION.api.riotgames.com/lol/match/v5/matches/by-puuid/PUUID/ids?start=0&count=AMOUNT&api_key=" + apiKey;
    private static final String matchURL = "https://REGION.api.riotgames.com/lol/match/v5/matches/MATCHID?api_key=" + apiKey;
    private static final String URLGetAllChampions = "https://ddragon.leagueoflegends.com/cdn/16.17.1/data/en_US/champion.json";
    private static final String URLGetSpecificChampion = "https://ddragon.leagueoflegends.com/cdn/16.17.1/data/en_US/champion/ID.json";
    private static final String URLGetActiveRegion = "https://europe.api.riotgames.com/riot/account/v1/region/by-game/lol/by-puuid/PUUID?api_key=" + apiKey;

    //ACCOUNT IDENTITY
    public AccountIdentityDTO getAccountIdentityFromPUUID(String puuid, String region) {
        String url = URLGetAccountIdentityFromPUUID;
        url = url.replace("PUUID", puuid);
        url = url.replace("REGION", region);
        return apiReader.getWithJackson(url, AccountIdentityDTO.class);
    }

    public AccountIdentityDTO getRiotAccountFromGameNameTaglineAndRegion(String region, String gameName, String tagLine) {
        String url = URLGetRiotAccountFromGameNameTaglineAndRegion;
        gameName = gameName.replace(" ", "%20");
        url = url.replace("REGION", region);
        url = url.replace("GAMENAME", gameName);
        url = url.replace("TAGLINE", tagLine);
        AccountIdentityDTO dto = apiReader.getWithJackson(url, AccountIdentityDTO.class);
        return dto;
    }

    //ACCOUNT STATE
    public AccountStateDTO getAccountState(String puuid, String region) {
        String url = URLGetAccountState;
        url = url.replace("PUUID", puuid);
        url = url.replace("REGION", region);
        return apiReader.getWithJackson(url, AccountStateDTO.class);
    }

    //ACCOUNT ENTRIES
    public AccountEntriesDTO[] getAccountEntries(String puuid, String region) {
        String url = URLGetAccountEntries;
        url = url.replace("PUUID", puuid);
        url = url.replace("REGION", region);
        return apiReader.getWithJackson(url, AccountEntriesDTO[].class);
    }

    //MATCH HISTORY IN IDs
    public String[] getMatchHistory(String region, String puuid, String amount) {
        String url = matchHistoryURL;
        url = url.replace("REGION", region);
        url = url.replace("PUUID", puuid);
        url = url.replace("AMOUNT", amount);
        return apiReader.getWithJackson(url, String[].class);
    }

    //MATCH FROM ID
    public MatchDTO getMatchFromId(String region, String matchId) {
        String url = matchURL;
        url = url.replace("REGION", region);
        url = url.replace("MATCHID", matchId);
        return apiReader.getWithJackson(url, MatchDTO.class);
    }

    //MATCH HISTORY IN LIST
    public List<MatchDTO> getMatchHistoryInMatchDTOs(String[] matchIds, String region) {
        List<MatchDTO> matchHistory = new ArrayList<>();
        for (String matchId : matchIds) {
            MatchDTO match = getMatchFromId(region, matchId);
            matchHistory.add(match);
        }
        return matchHistory;
    }

    //PLAYER ACTIVE REGION
    public String getActiveRegion(String puuid) {
        String url = URLGetActiveRegion;
        url = url.replace("PUUID", puuid);
        JsonNode response = apiReader.getWithJackson(url, JsonNode.class);

        return response.get("region").asText();
    }

    //GET ALL CHAMPIONS
    public List<String> getAllChampionIdWithJackson() {
        List<String> nodeFromList = new ArrayList<>();
        try {
            JsonNode node = objectMapper.readTree(new URI(URLGetAllChampions).toURL());
            JsonNode data = node.get("data");
            data.elements().forEachRemaining(champion -> nodeFromList.add(champion.get("id").asText()));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        return nodeFromList;
    }

    public ChampionDTO getChampionWithId(String championId) {
        String url = URLGetSpecificChampion;
        String specificUrl = url.replace("ID", championId);
        ChampionDTO championDTO = apiReader.getWithJackson(specificUrl, ChampionDTO.class, "data", championId);
        return championDTO;
    }

    //ACCOUNT PFP
    public String getProfileIconFromId(int iconId) {
        String iconURL = "https://ddragon.leagueoflegends.com/cdn/16.19.1/img/profileicon/ID.png";
        return iconURL.replace("ID", "" + iconId);
    }

    //CONTINENT GET
    public String getContinentFromRegion(String region) {
        if (region.equals("euw1") ||
                region.equals("eun1") ||
                region.equals("tr1") ||
                region.equals("ru")) {
            return "europe";
        }

        if (region.equals("na1") ||
                region.equals("br1") ||
                region.equals("la1") ||
                region.equals("la2")) {
            return "americas";
        }

        if (region.equals("kr") ||
                region.equals("jp1")) {
            return "asia";
        }

        return "sea";
    }
}
