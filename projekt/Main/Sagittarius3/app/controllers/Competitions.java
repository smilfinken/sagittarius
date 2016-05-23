package controllers;

import models.*;
import views.html.*;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import org.joda.time.LocalTime;
import org.joda.time.format.*;

import it.innove.play.pdf.PdfGenerator;

public class Competitions extends Controller {
    @Inject
    private FormFactory formFactory;

    @Inject
    private PdfGenerator pdfGenerator;

    @Transactional
    public Result generateTestData() {
        User user;

        user = new User();
        user.email = "foo@foo";
        user.firstName = "foo";
        user.lastName = "foo";
        user.cardId = 12345;
        user.ranking = 1;
        user.club = "klubb 1";
        JPA.em().merge(user);

        user = new User();
        user.email = "bar@bar";
        user.firstName = "bar";
        user.lastName = "bar";
        user.cardId = 23456;
        user.ranking = 3;
        user.club = "klubb 2";
        JPA.em().merge(user);

        user = new User();
        user.email = "bork@bork";
        user.firstName = "bork";
        user.lastName = "bork";
        user.cardId = 34567;
        user.ranking = 2;
        user.senior = true;
        user.club = "klubb 2";
        JPA.em().merge(user);

        Competition competition = new Competition();
        competition.label = "test";
        competition.squadSize = 6;
        competition = JPA.em().merge(competition);

        Stage stage;

        stage = new Stage();
        stage.index = 1;
        stage.targetCount = 3;
        stage.points = false;
        competition.stages.add(JPA.em().merge(stage));

        stage = new Stage();
        stage.index = 2;
        stage.targetCount = 4;
        stage.points = false;
        competition.stages.add(JPA.em().merge(stage));

        stage = new Stage();
        stage.index = 3;
        stage.targetCount = 1;
        stage.points = true;
        competition.stages.add(JPA.em().merge(stage));

        Squad squad;

        squad = new Squad();
        squad.label = "1";
        squad.rollcall = "08:30";
        squad.slots = competition.squadSize;
        competition.squads.add(JPA.em().merge(squad));

        squad = new Squad();
        squad.label = "2";
        squad.rollcall = "08:40";
        squad.slots = competition.squadSize;
        competition.squads.add(JPA.em().merge(squad));

        return redirect(routes.Competitions.list());
    }

    @Transactional(readOnly = true)
    public Result list() {
        ArrayList<Competition> competitions = (ArrayList<Competition>)JPA.em().createQuery("SELECT c FROM Competition c", Competition.class).getResultList();
        return ok(competitionlist.render(competitions));
    }

    @Transactional(readOnly = true)
    public Result show(Long competitionId, String tab) {
        Competition competition = JPA.em().find(Competition.class, competitionId);
        return ok(competitionshow.render(competition, tab));
    }

    @Transactional(readOnly = true)
    public Result edit(Long competitionId) {
        if (competitionId != 0) {
            return ok(competitionedit.render(JPA.em().find(Competition.class, competitionId)));
        } else {
            return ok(competitionedit.render(new Competition()));
        }
    }

    @Transactional
    public Result save() {
        Competition requestData = formFactory.form(Competition.class).bindFromRequest().get();
        Competition competition = JPA.em().find(Competition.class, requestData.id);

        if (competition != null) {
            competition.copyValues(requestData);
        } else {
            competition = JPA.em().merge(requestData);
        }

        return redirect(routes.Competitions.show(competition.id, "information"));
    }

    @Transactional(readOnly = true)
    public Result printStartlist(Long competitionId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String printDate = dateFormat.format(Calendar.getInstance().getTime());
        String httpRequest = String.format("http://%s", request().host());
        return pdfGenerator.ok(startlistprint.render(competition.startlistEntries(), competition.label, competition.date(), printDate), httpRequest);
    }

    @Transactional(readOnly = true)
    public Result editStage(Long competitionId, Long stageId) {
        if (stageId != 0) {
            return ok(stageedit.render(competitionId, JPA.em().find(Stage.class, stageId)));
        } else {
            Competition competition = JPA.em().find(Competition.class, competitionId);
            Stage stage = new Stage(competition.stages.size() + 1);
            return ok(stageedit.render(competitionId, stage));
        }
    }

    @Transactional
    public Result saveStage() {
        DynamicForm dynamicData = formFactory.form().bindFromRequest();
        Long competitionId = Long.valueOf(dynamicData.get("competitionId"));
        Stage requestData = formFactory.form(Stage.class).bindFromRequest().get();
        Competition competition = JPA.em().find(Competition.class, competitionId);
        Stage stage = JPA.em().find(Stage.class, requestData.id);

        if (stage != null) {
            stage.copyValues(requestData);
        } else {
            competition.stages.add(JPA.em().merge(requestData));
        }

        return redirect(routes.Competitions.show(competitionId, "stages"));
    }

    @Transactional
    public Result deleteStage(Long competitionId, Long stageId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);
        competition.stages.remove(JPA.em().find(Stage.class, stageId));
        return redirect(routes.Competitions.show(competitionId, "stages"));
    }

    @Transactional(readOnly = true)
    public Result editSquad(Long competitionId, Long squadId) {
        if (squadId != 0) {
            return ok(squadedit.render(competitionId, JPA.em().find(Squad.class, squadId)));
        } else {
            Competition competition = JPA.em().find(Competition.class, competitionId);
            Squad squad = new Squad(competition.squadSize);
            return ok(squadedit.render(competitionId, squad));
        }
    }

    @Transactional
    public Result saveSquad() {
        DynamicForm dynamicData = formFactory.form().bindFromRequest();
        Long competitionId = Long.valueOf(dynamicData.get("competitionId"));
        Competition competition = JPA.em().find(Competition.class, competitionId);
        Squad requestData = formFactory.form(Squad.class).bindFromRequest().get();
        Squad squad = JPA.em().find(Squad.class, requestData.id);

        if (squad != null) {
            squad.copyValues(requestData);
        } else {
            competition.squads.add(JPA.em().merge(requestData));
        }

        return redirect(routes.Competitions.show(competitionId, "squads"));
    }

    @Transactional
    public Result deleteSquad(Long competitionId, Long squadId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);
        competition.squads.remove(JPA.em().find(Squad.class, squadId));
        return redirect(routes.Competitions.show(competitionId, "squads"));
    }

    @Transactional
    public Result generateSquads() {
        DynamicForm dynamicData = formFactory.form().bindFromRequest();
        Long competitionId = Long.valueOf(dynamicData.get("competitionId"));
        String action = dynamicData.get("action");

        switch (action) {
            case "add":
                return ok(squadgenerate.render(competitionId));
            case "save":
                int squadCount = Integer.parseInt(dynamicData.get("squadCount"));
                int interval = Integer.parseInt(dynamicData.get("interval"));
                DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
                LocalTime rollcall = formatter.parseLocalTime(dynamicData.get("startTime"));

                Competition competition = JPA.em().find(Competition.class, competitionId);
                for (int i = 0; i < squadCount; i++) {
                    Squad squad = new Squad();
                    squad.label = Integer.toString(i + 1);
                    squad.slots = competition.squadSize;
                    squad.rollcall = formatter.print(rollcall);

                    competition.squads.add(JPA.em().merge(squad));
                    rollcall = rollcall.plusMinutes(interval);
                }
        }
        return redirect(routes.Competitions.show(competitionId, "squads"));
    }

    @Transactional
    public Result printSquad(Long competitionId, Long squadId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);
        Squad squad = JPA.em().find(Squad.class, squadId);

        return ok(squadprint.render(squad, competition.stages));
    }

    @Transactional(readOnly = true)
    public Result addCompetitor(Long competitionId) {
        ArrayList<Squad> allSquads = (ArrayList<Squad>) JPA.em().createQuery("SELECT s from Squad s ORDER BY s.rollcall", Squad.class).getResultList();
        ArrayList<User> users = (ArrayList<User>) JPA.em().createQuery("SELECT u from User u ORDER BY u.firstName, u.lastName", User.class).getResultList();

        Competition competition = JPA.em().find(Competition.class, competitionId);
        ArrayList<Squad> squads = new ArrayList<>();
        for (Squad squad: allSquads) {
            if (squad.competitors() < competition.squadSize) {
                squads.add(squad);
            }
        }
        return ok(competitoradd.render(competitionId, squads, users));
    }

    @Transactional
    public Result saveCompetitor() {
        DynamicForm dynamicData = formFactory.form().bindFromRequest();
        Long competitionId = Long.valueOf(dynamicData.get("competitionId"));
        Long squadId = Long.valueOf(dynamicData.get("squadId"));
        Squad squad = JPA.em().find(Squad.class, squadId);
        Long userId = Long.valueOf(dynamicData.get("userId"));
        User user = JPA.em().find(User.class, userId);

        Competitor competitor = formFactory.form(Competitor.class).bindFromRequest().get();
        competitor.user = user;
        squad.competitors.add(JPA.em().merge(competitor));

        return redirect(routes.Competitions.show(competitionId, "competitors"));
    }

    @Transactional
    public Result moveCompetitor(Long competitionId, Long squadId, Long competitorId) {
        return redirect(routes.Competitions.editSquad(competitionId, squadId));
    }

    @Transactional
    public Result removeCompetitor(Long competitionId, Long squadId, Long competitorId) {
        Squad squad = JPA.em().find(Squad.class, squadId);
        Competitor competitor = JPA.em().find(Competitor.class, competitorId);
        squad.competitors.remove(competitor);

        return redirect(routes.Competitions.show(competitionId, "competitors"));
    }

    @Transactional(readOnly = true)
    public Result editTeam(Long competitionId, Long teamId) {
        return redirect(routes.Competitions.show(competitionId, "teams"));
    }

    @Transactional
    public Result saveTeam() {
        return redirect(routes.Competitions.list());
    }

    @Transactional(readOnly = true)
    public Result export(Long competitionId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);

        return ok(Json.toJson(competition));
    }
}
