package app.controllers;

import app.client.RiotApiClient;
import app.config.HibernateConfig;
import app.daos.ChampionDAO;
import app.dtos.championdtos.ChampionDTO;
import app.entities.Champion;
import app.mappers.ChampionMapper;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class ChampionController {
    private static final Logger logger = LoggerFactory.getLogger(ChampionController.class);
    RiotApiClient riotApiClient = new RiotApiClient();
    ChampionMapper championMapper = new ChampionMapper();
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    ChampionDAO championDAO = new ChampionDAO(emf);


    public void updateChampions (Context ctx) {
        List<String> championIds = riotApiClient.getAllChampionIdWithJackson();

        for (String championId : championIds) {
            ChampionDTO championDTO = riotApiClient.getChampionWithId(championId);
            Champion champion = championMapper.toEntity(championDTO);
            championDAO.update(champion);
        }

        Set<Champion> champions = championDAO.get();

        logger.info("All Champions successfully updated!");
        ctx.status(200);
        ctx.json(champions);
    }
}
