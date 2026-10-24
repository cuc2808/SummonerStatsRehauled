package app.controllers;


import app.client.RiotApiClient;
import app.daos.MatchPreviewDAO;
import app.dtos.matchdtos.MatchDTO;
import app.dtos.matchdtos.MatchPreviewDTO;
import app.entities.MatchPreview;
import io.javalin.http.Context;

public class MatchController {
    private RiotApiClient riotApiClient = new RiotApiClient();

    public void getMatchDetails(Context ctx) {
        MatchPreviewDTO matchPreviewDTO = ctx.bodyAsClass(MatchPreviewDTO.class);

        MatchDTO matchDTO = riotApiClient.getMatchFromId(riotApiClient.getContinentFromRegion(matchPreviewDTO.region()), matchPreviewDTO.matchId());

        System.out.println("MATCH DETAILS ID: " + matchDTO.getInfo().getGameId() + ", REGION " + matchPreviewDTO.region());
        ctx.status(200).json(matchDTO);
    }
}
