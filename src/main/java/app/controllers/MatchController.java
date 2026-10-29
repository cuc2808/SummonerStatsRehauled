package app.controllers;


import app.client.RiotApiClient;
import app.daos.MatchPreviewDAO;
import app.dtos.matchdtos.MatchDTO;
import app.dtos.matchdtos.MatchPreviewDTO;
import app.entities.MatchPreview;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchController {
    private RiotApiClient riotApiClient = new RiotApiClient();
    private static final Logger logger = LoggerFactory.getLogger(MatchController.class);

    public void getMatchDetails(Context ctx) {
        MatchPreviewDTO matchPreviewDTO = ctx.bodyAsClass(MatchPreviewDTO.class);

        MatchDTO matchDTO = riotApiClient.getMatchFromId(riotApiClient.getContinentFromRegion(matchPreviewDTO.region()), matchPreviewDTO.matchId());

        logger.info("MATCH DETAILS ID: {}, REGION {}", matchDTO.getInfo().getGameId(), matchPreviewDTO.region());
        ctx.status(200).json(matchDTO);
    }
}
