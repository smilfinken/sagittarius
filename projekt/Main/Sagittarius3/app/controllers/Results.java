package controllers;

import models.*;
import play.Logger;
import views.html.*;

import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import it.innove.play.pdf.PdfGenerator;

public class Results extends Controller {
    @Inject
    private FormFactory formFactory;

    @Inject
    private PdfGenerator pdfGenerator;

    @Transactional(readOnly = true)
    public Result list(Long competitionId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);

        HashMap<String, Integer> top = new HashMap<String, Integer>();
        HashMap<String, Integer> limit = new HashMap<String, Integer>();
        ArrayList<ClassResults> individualResults = competition.calculateIndividualResults(top, limit);
        ArrayList<TeamResults> teamResults = competition.calculateTeamResults();

        HashMap<Integer, Boolean> flags = new HashMap<Integer, Boolean>();
        for (Stage stage : competition.stages) {
            flags.put(stage.index, stage.points);
        }

        return ok(resultlist.render(individualResults, teamResults, flags, top, limit));
    }

    @Transactional(readOnly = true)
    public Result display(Long competitionId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);

        ArrayList<Score> scores = new ArrayList<Score>();
        for (Squad squad : competition.squads) {
            for (Competitor competitor : squad.competitors) {
                if (competitor.scored()) {
                    scores.add(new Score(competitionId, competitor, competition.championship, competition.stages.size()));
                }
            }
        }

        HashMap<String, Integer> top = new HashMap<String, Integer>();
        HashMap<String, Integer> limit = new HashMap<String, Integer>();
        ArrayList<ClassResults> individualResults = competition.calculateIndividualResults(top, limit);

        HashMap<Integer, Boolean> flags = new HashMap<Integer, Boolean>();
        for (Stage stage : competition.stages) {
            flags.put(stage.index, stage.points);
        }

        return ok(resultdisplay.render(individualResults, flags, top, limit));
    }

    @Transactional(readOnly = true)
    public Result print(Long competitionId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);

        ArrayList<Score> scores = new ArrayList<Score>();
        for (Squad squad : competition.squads) {
            for (Competitor competitor : squad.competitors) {
                if (competitor.scored()) {
                    scores.add(new Score(competitionId, competitor, competition.championship, competition.stages.size()));
                }
            }
        }

        HashMap<String, Integer> top = new HashMap<String, Integer>();
        HashMap<String, Integer> limit = new HashMap<String, Integer>();
        ArrayList<ClassResults> individualResults = competition.calculateIndividualResults(top, limit);
        ArrayList<TeamResults> teamResults = competition.calculateTeamResults();

        HashMap<Integer, Boolean> flags = new HashMap<Integer, Boolean>();
        for (Stage stage : competition.stages) {
            flags.put(stage.index, stage.points);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String printDate = dateFormat.format(Calendar.getInstance().getTime());
        String httpRequest = String.format("http://%s", request().host());
        return pdfGenerator.ok(resultprint.render(individualResults, teamResults, flags, top, limit, competition.label, competition.dateString(), printDate, competition.maxScore(), competition.entries()), httpRequest);
    }

    @Transactional()
    private void addScores(Long competitorId, Integer stageIndex, ScoreItem scoreItem) {
        Competitor competitor = JPA.em().find(Competitor.class, competitorId);

        if (competitor == null) {
            Logger.error("Failed to find competitor");
        } else {
            StageScore stageScore = competitor.getStageScore(stageIndex);
            stageScore.targetScores.clear();
            for (Integer value: scoreItem.hits) {
                stageScore.targetScores.add(JPA.em().merge(new TargetScore(value)));
            }
            stageScore.points = scoreItem.points == null ? 0 : scoreItem.points;
        }
    }

    @Transactional()
    public Result scoreSquad() {
        DynamicForm dynamicData = formFactory.form().bindFromRequest();

        Long competitionId = Long.valueOf(session().get("competitionId"));
        Competition competition = JPA.em().find(Competition.class, competitionId);

        Long squadId = Long.valueOf(dynamicData.get("squadId"));
        Squad squad = JPA.em().find(Squad.class, squadId);

        String action = dynamicData.get("action");
        switch (action) {
            case "show":
                if (squad != null && !squad.scored()) {
                    for (Competitor competitor : squad.competitors) {
                        if (!competitor.scored()) {
                            if (competitor.scores.isEmpty()) {
                                for(Stage stage: competition.stages) {
                                    competitor.scores.add(JPA.em().merge(new StageScore(stage.index)));
                                }
                            }
                            return ok(squadscoring.render(competition, squad, competitor, 1));
                        }
                    }
                }

                return redirect(routes.Competitions.show(competition.id, "squads"));
            case "save":
                Long competitorId = Long.valueOf(dynamicData.get("competitorId"));
                Integer stageIndex = Integer.valueOf(dynamicData.get("stageIndex"));
                ScoreItem scoreItem = formFactory.form(ScoreItem.class).bindFromRequest().get();
                if (stageIndex <= 0 || stageIndex > competition.stages.size()) {
                    Logger.error("Invalid stage index " + stageIndex);
                } else {
                    addScores(competitorId, stageIndex, scoreItem);
                }

                for (Competitor competitor: squad.competitors) {
                    if (!competitor.scored()) {
                        if (competitor.scores.isEmpty()) {
                            for (Stage stage : competition.stages) {
                                competitor.scores.add(JPA.em().merge(new StageScore(stage.index)));
                            }
                        }
                        for (Stage stage : competition.stages) {
                            if (!competitor.getStageScore(stage.index).scored()) {
                                return ok(squadscoring.render(competition, squad, competitor, stage.index));
                            }
                        }
                    }
                }

                return redirect(routes.Competitions.show(competition.id, "squads"));
            default:
                return redirect(routes.Competitions.show(competition.id, "squads"));
        }
    }

    @Transactional()
    public Result editResult(Long competitorId) {
        return redirect(routes.Results.list(Long.valueOf(session().get("competitionId"))));
    }

    @Transactional()
    public Result deleteResult(Long competitorId) {
        Competitor competitor = JPA.em().find(Competitor.class, competitorId);
        competitor.scores.clear();

        return redirect(routes.Results.list(Long.valueOf(session().get("competitionId"))));
    }
}
