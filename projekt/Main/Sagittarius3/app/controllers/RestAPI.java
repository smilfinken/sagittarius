package controllers;

import play.Logger;

import models.*;
import play.db.jpa.Transactional;
import views.html.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import play.db.jpa.JPA;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

//import javax.inject.Inject;

public class RestAPI extends Controller {
    @Transactional(readOnly = true)
    public Result listCompetitions() {
        List<Competition> competitions = JPA.em().createQuery("SELECT c FROM Competition c", Competition.class).getResultList();
        ArrayNode result = Json.newArray();
        competitions.forEach(competition -> {
            result.add(competition.toJson());
        });
        return ok(result);
    }

    @Transactional(readOnly = true)
    public Result getCompetition(Long competitionId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);
        return ok(competition.toJson());
    }

    @Transactional(readOnly = true)
    public Result exportCompetition(Long competitionId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);
        return ok(Json.toJson(competition));
    }

    @Transactional(readOnly = true)
    public Result getSquads(Long competitionId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);
        ArrayNode result = Json.newArray();
        competition.squads.forEach(squad -> {
            result.add(squad.toJson());
        });
        return ok(result);
    }

    @Transactional(readOnly = true)
    public Result getSquad(Long competitionId, Long squadId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);
        Squad squad = JPA.em().find(Squad.class, squadId);
        return ok(squad.toJson().put("capacity", competition.squadSize));
    }

    @Transactional(readOnly = true)
    public Result getCompetitors(Long competitionId, Long squadId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);
        Squad squad = JPA.em().find(Squad.class, squadId);
        ArrayNode result = Json.newArray();
        squad.competitors.forEach(competitor -> {
            result.add(competitor.toJson());
        });
        return ok(result);
    }

    @Transactional(readOnly = true)
    public Result getStages(Long competitionId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);
        ArrayNode result = Json.newArray();
        competition.stages.forEach(stage -> {
            result.add(stage.toJson());
        });
        return ok(result);
    }

    @Transactional(readOnly = true)
    public Result getStage(Long competitionId, Long stageId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);
        Stage stage = JPA.em().find(Stage.class, stageId);
        return ok(stage.toJson());
    }

    @Transactional()
    @BodyParser.Of(BodyParser.Json.class)
    public Result scoreCompetitor(Long competitionId, Long competitorId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);
        Competitor competitor = JPA.em().find(Competitor.class, competitorId);

        if (competition == null) {
            String error = Messages.get("message.error.invalididfor", Messages.get("label.common.competition"));
            Logger.error(error);
            return internalServerError(error);
        }

        if (competitor == null) {
            String error = Messages.get("message.error.invalididfor", Messages.get("label.common.competitor"));
            Logger.error(error);
            return internalServerError(error);
        }

        if (competitor.scores.isEmpty()) {
            competition.stages.forEach(stage -> {
                competitor.scores.add(JPA.em().merge(new StageScore(stage.index)));
            });
        }

        JsonNode score = request().body().asJson();
        JsonNode stageScores = score.findPath("stageScores");
        if (stageScores == null) {
            String error = Messages.get("message.error.failedtoparsejson");
            Logger.error(error);
            return internalServerError(error);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<StageScore> stages = objectMapper.readValue(stageScores.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, StageScore.class));
            stages.forEach(stage -> {
                if (stage.index < 1 || stage.index > competition.stages.size()) {
                    Logger.error("invalid stage index = " + stage.index);
                } else {
                    StageScore stageScore = competitor.getStageScore(stage.index);
                    if (stageScore.index != stage.index) {
                        Logger.error("failed to find stage score with index = " + stage.index);
                    } else {
                        stageScore.targetScores.clear();
                        stage.targetScores.forEach(targetScore -> {
                            stageScore.targetScores.add(JPA.em().merge(targetScore));
                        });
                        stageScore.points = stage.points;
                    }
                }
            });
        } catch (Exception exception) {
            String error = Messages.get("message.error.failedtoparsejson" + ": " + exception);
            Logger.error(error);
            return internalServerError(error);
        }
        if (competitor.scored()) {
            Logger.info("successfully scored competitor with ID = " + competitor.id);
            ObjectNode result = Json.newObject().put("competitorId", competitor.id);
            result.put("scores", Json.toJson(competitor.scores));
            return ok(result);
        } else {
            String error = Messages.get("message.error.failedtoregisterfor", Messages.get("label.competitor.score"));
            Logger.error(error);
            return internalServerError(Json.newObject().put("status", "error").put("message", error));
        }
    }
}
