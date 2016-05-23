package controllers;

import models.*;
import views.html.*;

import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.Logger;
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
        ArrayList<ClassResults> results = competition.calculateResults(top, limit);

        HashMap<Integer, Boolean> flags = new HashMap<Integer, Boolean>();
        for (Stage stage : competition.stages) {
            flags.put(stage.index, stage.points);
        }

        return ok(resultlist.render(results, flags, top, limit));
    }

    @Transactional(readOnly = true)
    public Result display(Long competitionId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);

        ArrayList<Score> scores = new ArrayList<Score>();
        for (Squad squad : competition.squads) {
            for (Competitor competitor : squad.competitors) {
                if (competitor.scored) {
                    scores.add(new Score(competitionId, competitor));
                }
            }
        }

        HashMap<String, Integer> top = new HashMap<String, Integer>();
        HashMap<String, Integer> limit = new HashMap<String, Integer>();
        ArrayList<ClassResults> results = competition.calculateResults(top, limit);

        HashMap<Integer, Boolean> flags = new HashMap<Integer, Boolean>();
        for (Stage stage : competition.stages) {
            flags.put(stage.index, stage.points);
        }

        return ok(resultdisplay.render(results, flags, top, limit));
    }

    @Transactional(readOnly = true)
    public Result print(Long competitionId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);

        ArrayList<Score> scores = new ArrayList<Score>();
        for (Squad squad : competition.squads) {
            for (Competitor competitor : squad.competitors) {
                if (competitor.scored) {
                    scores.add(new Score(competitionId, competitor));
                }
            }
        }

        HashMap<String, Integer> top = new HashMap<String, Integer>();
        HashMap<String, Integer> limit = new HashMap<String, Integer>();
        ArrayList<ClassResults> results = competition.calculateResults(top, limit);

        HashMap<Integer, Boolean> flags = new HashMap<Integer, Boolean>();
        for (Stage stage : competition.stages) {
            flags.put(stage.index, stage.points);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String printDate = dateFormat.format(Calendar.getInstance().getTime());
        String httpRequest = String.format("http://%s", request().host());
        return pdfGenerator.ok(resultprint.render(results, flags, top, limit, competition.label, competition.date(), printDate, competition.maxScore(), competition.entries()), httpRequest);
    }

    @Transactional()
    public Result scoreSquad() {
        DynamicForm dynamicData = formFactory.form().bindFromRequest();

        Long competitionId = Long.valueOf(dynamicData.get("competitionId"));
        Competition competition = JPA.em().find(Competition.class, competitionId);

        Long squadId = Long.valueOf(dynamicData.get("squadId"));
        Squad squad = JPA.em().find(Squad.class, squadId);

        String action = dynamicData.get("action");
        switch (action) {
            case "show":
                if (!squad.scored()) {
                    for (Competitor competitor : squad.competitors) {
                        if (!competitor.scored) {
                            return ok(squadscoring.render(competition, squad, competitor));
                        }
                    }
                }

                return ok(competitionedit.render(competition));
            case "save":
                Long competitorId = Long.valueOf(dynamicData.get("competitorId"));
                Competitor scoringCompetitor = JPA.em().find(Competitor.class, competitorId);

                ScoreSet scoreSet = formFactory.form(ScoreSet.class).bindFromRequest().get();

                if (scoreSet.index.length > 0 && scoringCompetitor != null) {
                    scoringCompetitor.scores.clear();
                    for (int i = 0; i < scoreSet.index.length; i++) {
                        if (scoreSet.index[i] != null) {
                            StageScore score = new StageScore();
                            score.index = scoreSet.index[i];
                            score.shots = scoreSet.shots[i] == null ? 0 : scoreSet.shots[i];
                            score.targets = scoreSet.targets[i] == null ? 0 : scoreSet.targets[i];
                            score.points = scoreSet.points[i] == null ? 0 : scoreSet.points[i];
                            scoringCompetitor.scores.add(JPA.em().merge(score));
                        }
                    }

                    scoringCompetitor.scored = true;
                }

                for (Competitor competitor: squad.competitors) {
                    if (!competitor.scored) {
                        return ok(squadscoring.render(competition, squad, competitor));
                    }
                }

                return ok(competitionedit.render(competition));
            default:
                return ok(competitionedit.render(competition));
        }
    }

    @Transactional()
    public Result editResult(Long competitionId, Long competitorId) {
        return redirect(routes.Results.list(competitionId));
    }

    @Transactional()
    public Result deleteResult(Long competitionId, Long competitorId) {
        Competitor competitor = JPA.em().find(Competitor.class, competitorId);
        competitor.scores.clear();
        competitor.scored = false;

        return redirect(routes.Results.list(competitionId));
    }
}
