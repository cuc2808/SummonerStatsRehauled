package app.dtos.matchdtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class ParticipantDTO {
    @JsonProperty("allInPings")
    int allInPings;
    @JsonProperty("assistMePings")
    int assistMePings;
    @JsonProperty("assists")
    int assists;
    @JsonProperty("baronKills")
    int baronKills;
    @JsonProperty("bountyLevel")
    int bountyLevel;
    @JsonProperty("champExperience")
    int champExperience;
    @JsonProperty("champLevel")
    int champLevel;
    @JsonProperty("championId")
    int championId;
    @JsonProperty("championName")
    String championName;
    @JsonProperty("commandPings")
    int commandPings;
    @JsonProperty("championTransform")
    int championTransform;
    @JsonProperty("consumablesPurchased")
    int consumablesPurchased;
    //UNUSED ****
    //@JsonProperty("challenges")
    //ChallengesDTO challenges;
    @JsonProperty("damageDealtToBuildings")
    int damageDealtToBuildings;
    @JsonProperty("damageDealtToObjectives")
    int damageDealtToObjectives;
    @JsonProperty("damageDealtToTurrets")
    int damageDealtToTurrets;
    @JsonProperty("damageSelfMitigated")
    int damageSelfMitigated;
    @JsonProperty("deaths")
    int deaths;
    @JsonProperty("detectorWardsPlaced")
    int detectorWardsPlaced;
    @JsonProperty("doubleKills")
    int doubleKills;
    @JsonProperty("dragonKills")
    int dragonKills;
    @JsonProperty("eligibleForProgression")
    boolean eligibleForProgression;
    @JsonProperty("enemyMissingPings")
    int enemyMissingPings;
    @JsonProperty("enemyVisionPings")
    int enemyVisionPings;
    @JsonProperty("firstBloodAssist")
    boolean firstBloodAssist;
    @JsonProperty("firstBloodKill")
    boolean firstBloodKill;
    @JsonProperty("firstTowerAssist")
    boolean firstTowerAssist;
    @JsonProperty("firstTowerKill")
    boolean firstTowerKill;
    @JsonProperty("gameEndedInEarlySurrender")
    boolean gameEndenInEarlySurrender;
    @JsonProperty("gameEndedInSurrender")
    boolean gameEndedInSurrender;
    @JsonProperty("holdPings")
    int holdPings;
    @JsonProperty("getBackPings")
    int getBackPings;
    @JsonProperty("goldEarned")
    int goldEarned;
    @JsonProperty("goldSpent")
    int goldSpent;
    @JsonProperty("individualPosition")
    String individualPosition;
    @JsonProperty("inhibitorKills")
    int inhibitorKills;
    @JsonProperty("inhibitorTakedowns")
    int inhibitorTakedowns;
    @JsonProperty("inhibitorsLost")
    int inhibitorsLost;
    @JsonProperty("item0")
    int item0;
    @JsonProperty("item1")
    int item1;
    @JsonProperty("item2")
    int item2;
    @JsonProperty("item3")
    int item3;
    @JsonProperty("item4")
    int item4;
    @JsonProperty("item5")
    int item5;
    @JsonProperty("item6")
    int item6;
    @JsonProperty("itemsPurchased")
    int itemsPurchased;
    @JsonProperty("killingSprees")
    int killingSprees;
    @JsonProperty("kills")
    int kills;
    @JsonProperty("lane")
    String lane;
    @JsonProperty("largestCriticalStrike")
    int largestCriticalStrike;
    @JsonProperty("largestKillingSpree")
    int largestKillingSpree;
    @JsonProperty("largestMultiKill")
    int largestMultiKill;
    @JsonProperty("longestTimeSpentLiving")
    int longestTimeSpentLiving;
    @JsonProperty("magicDamageDealt")
    int magicDamageDealt;
    @JsonProperty("magicDamageDealtToChampions")
    int magicDamageDealtToChampions;
    @JsonProperty("magicDamageTaken")
    int magicDamageTaken;
    //UNUSED ****
    //    @JsonProperty("missions")
    //    MissionsDTO missionsDTO;
    @JsonProperty("neutralMinionsKilled")
    int neutralMinionsKilled;
    @JsonProperty("needVisionPings")
    int needVisionPings;
    @JsonProperty("nexusKills")
    int nexusKills;
    @JsonProperty("nexusTakedowns")
    int nexusTakedowns;
    @JsonProperty("nexusLost")
    int nexusLost;
    @JsonProperty("objectivesStolen")
    int objectivesStolen;
    @JsonProperty("objectivesStolenAssists")
    int objectivesStolenAssists;
    @JsonProperty("onMyWayPings")
    int onMyWayPings;
    @JsonProperty("participantId")
    int participantId;
    @JsonProperty("playerScore0")
    int playerScore0;
    @JsonProperty("playerScore1")
    int playerScore1;
    @JsonProperty("playerScore2")
    int playerScore2;
    @JsonProperty("playerScore3")
    int playerScore3;
    @JsonProperty("playerScore4")
    int playerScore4;
    @JsonProperty("playerScore5")
    int playerScore5;
    @JsonProperty("playerScore6")
    int playerScore6;
    @JsonProperty("playerScore7")
    int playerScore7;
    @JsonProperty("playerScore8")
    int playerScore8;
    @JsonProperty("playerScore9")
    int playerScore9;
    @JsonProperty("playerScore10")
    int playerScore10;
    @JsonProperty("playerScore11")
    int playerScore11;
    @JsonProperty("pentaKills")
    int pentaKills;
    //UNUSED ***
    //@JsonProperty("perks")
    //PerksDTO perks;
    @JsonProperty("physicalDamageDealt")
    int physicalDamageDealt;
    @JsonProperty("physicalDamageDealtToChampions")
    int physicalDamageDealtToChampions;
    @JsonProperty("physicalDamageTaken")
    int physicalDamageTaken;
    @JsonProperty("placement")
    int placement;
    @JsonProperty("playerAugment1")
    int playerAugment1;
    @JsonProperty("playerAugment2")
    int playerAugment2;
    @JsonProperty("playerAugment3")
    int playerAugment3;
    @JsonProperty("playerAugment4")
    int playerAugment4;
    @JsonProperty("playerSubteamId")
    int playerSubteamId;
    @JsonProperty("pushPings")
    int pushPings;
    @JsonProperty("profileIcon")
    int profileIcon;
    @JsonProperty("puuid")
    String puuid;
    @JsonProperty("quadraKills")
    int quadraKills;
    @JsonProperty("riotIdGameName")
    String riotIdGameName;
    @JsonProperty("riotIdTagline")
    String riotIdTagline;
    @JsonProperty("role")
    String role;
    @JsonProperty("sightWardsBoughtInGame")
    int sightWardsBoughtInGame;
    @JsonProperty("spell1Casts")
    int spell1Casts;
    @JsonProperty("spell2Casts")
    int spell2Casts;
    @JsonProperty("spell3Casts")
    int spell3Casts;
    @JsonProperty("spell4Casts")
    int spell4Casts;
    @JsonProperty("subteamPlacement")
    int subteamPlacement;
    @JsonProperty("summoner1Casts")
    int summoner1Casts;
    @JsonProperty("summoner1Id")
    int summoner1Id;
    @JsonProperty("summoner2Casts")
    int summoner2Casts;
    @JsonProperty("summoner2Id")
    int summoner2Id;
    @JsonProperty("summonerId")
    String summonerId;
    @JsonProperty("summonerLevel")
    int summonerLevel;
    @JsonProperty("summonerName")
    String summonerName;
    @JsonProperty("teamEarlySurrendered")
    boolean teamEarlySurrendered;
    @JsonProperty("teamId")
    int teamId;
    @JsonProperty("teamPosition")
    String teamPosition;
    @JsonProperty("timeCCingOthers")
    int timeCCingOthers;
    @JsonProperty("timePlayed")
    int timePlayed;
    @JsonProperty("totalAllyJungleMinionsKilled")
    int totalAllyJungleMinionsKilled;
    @JsonProperty("totalDamageDealt")
    int totalDamageDealt;
    @JsonProperty("totalDamageDealtToChampions")
    int totalDamageDealtToChampions;
    @JsonProperty("totalDamageShieldedOnTeammates")
    int totalDamageShieldedOnTeammates;
    @JsonProperty("totalDamageTaken")
    int totalDamageTaken;
    @JsonProperty("totalEnemyJungleMinionsKilled")
    int totalEnemyJungleMinionsKilled;
    @JsonProperty("totalHeal")
    int totalHeal;
    @JsonProperty("totalHealsOnTeammates")
    int totalHealsOnTeammates;
    @JsonProperty("totalMinionsKilled")
    int totalMinionsKilled;
    @JsonProperty("totalTimeCCDealt")
    int totalTimeCCDealt;
    @JsonProperty("totalTimeSpentDead")
    int totalTimeSpentDead;
    @JsonProperty("totalUnitsHealed")
    int totalUnitsHealed;
    @JsonProperty("tripleKills")
    int tripleKills;
    @JsonProperty("trueDamageDealt")
    int trueDamageDealt;
    @JsonProperty("trueDamageDealtToChampions")
    int trueDamageDealtToChampions;
    @JsonProperty("trueDamageTaken")
    int trueDamageTaken;
    @JsonProperty("turretKills")
    int turretKills;
    @JsonProperty("turretTakedowns")
    int turretTakedowns;
    @JsonProperty("turretsLost")
    int turretsLost;
    @JsonProperty("unrealKills")
    int unrealKills;
    @JsonProperty("visionScore")
    int visionScore;
    @JsonProperty("visionClearedPings")
    int visionClearedPings;
    @JsonProperty("visionWardsBoughtInGame")
    int visionWardsBoughtInGame;
    @JsonProperty("wardsKilled")
    int wardsKilled;
    @JsonProperty("wardsPlaced")
    int wardsPlaced;
    @JsonProperty("win")
    boolean win;
}
