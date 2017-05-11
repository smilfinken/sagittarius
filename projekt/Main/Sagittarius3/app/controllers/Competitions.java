package controllers;

import play.Logger;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import models.*;
import play.db.jpa.Transactional;
import views.html.*;

import play.db.jpa.JPA;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import it.innove.play.pdf.PdfGenerator;

public class Competitions extends Controller {
    @Inject
    private FormFactory formFactory;

    @Inject
    private PdfGenerator pdfGenerator;

    private boolean ValidateBookingTime(Competition competition, Squad selectedSquad,  User user) {
        return ValidateBookingTime(competition, selectedSquad, user, null);
    }

    private boolean ValidateBookingTime(Competition competition, Squad selectedSquad,  User user, Squad ignoreSquad) {
        for (Squad squad : competition.squads) {
            if (!squad.equals(ignoreSquad)) {
                for (Competitor competitor : squad.competitors) {
                    if (competitor.user.equals(user)) {
                        DateTime backwardLimit = squad.rollcall.minusMinutes(90);
                        DateTime forwardLimit = squad.rollcall.plusMinutes(90);
                        if (!(selectedSquad.rollcall.isBefore(backwardLimit) || selectedSquad.rollcall.isAfter(forwardLimit))) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    @Transactional
    public Result generateTestData() {
        CompetitionSubclass competitionSubclass;

        competitionSubclass = new CompetitionSubclass("J", "Junior");
        JPA.em().merge(competitionSubclass);

        competitionSubclass = new CompetitionSubclass("D", "Dam");
        JPA.em().merge(competitionSubclass);

        competitionSubclass = new CompetitionSubclass("VY", "Veteran Yngre");
        JPA.em().merge(competitionSubclass);

        competitionSubclass = new CompetitionSubclass("VÄ", "Veteran Äldre");
        JPA.em().merge(competitionSubclass);

        CompetitionClass competitionClass;

        competitionClass = new CompetitionClass("C", "Vapengrupp C");
        JPA.em().merge(competitionClass);

        competitionClass = new CompetitionClass("B", "Vapengrupp B");
        JPA.em().merge(competitionClass);

        competitionClass = new CompetitionClass("A", "Vapengrupp A");
        JPA.em().merge(competitionClass);

        competitionClass = new CompetitionClass("R", "Vapengrupp R");
        CompetitionClass mergedCompetitionClass = JPA.em().merge(competitionClass);

        competitionClass = new CompetitionClass("S", "Vapengrupp S");
        JPA.em().merge(competitionClass);

        CompetitionRanking competitionRanking;

        competitionRanking = new CompetitionRanking(1, "");
        CompetitionRanking ranking1 = JPA.em().merge(competitionRanking);

        competitionRanking = new CompetitionRanking(2, "");
        CompetitionRanking ranking2 = JPA.em().merge(competitionRanking);

        competitionRanking = new CompetitionRanking(3, "Riksmästarklass");
        CompetitionRanking ranking3 = JPA.em().merge(competitionRanking);

        /*
        User user;

        user = new User("foo@foo", "foo", "foo", 12345, ranking1, "klubb 1");
        JPA.em().merge(user);

        user = new User("bar@bar", "bar", "bar", 23456, ranking3, "klubb 2");
        JPA.em().merge(user);

        user = new User("bork@bork", "bork", "bork", 34567, ranking2, "klubb 2");
        User mergedUser = JPA.em().merge(user);

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

        DateTime competitionDate = new DateTime(competition.competitionDate).withHourOfDay(0).withMinuteOfHour(0);
        Squad squad;

        squad = new Squad();
        squad.label = "1";
        squad.rollcall = competitionDate.withHourOfDay(8).withMinuteOfHour(30);
        squad.slots = competition.squadSize;
        competition.squads.add(JPA.em().merge(squad));

        squad = new Squad();
        squad.label = "2";
        squad.rollcall = competitionDate.withHourOfDay(8).withMinuteOfHour(40);
        squad.slots = competition.squadSize;
        Squad mergedSquad = JPA.em().merge(squad);
        competition.squads.add(mergedSquad);

        Team team;
        team = new Team();
        team.label = "B0rked";
        team.club = "Klubb 2";
        Team mergedTeam = JPA.em().merge(team);
        competition.teams.add(mergedTeam);

        Competitor competitor = new Competitor(JPA.em().merge(mergedCompetitionClass));
        competitor.user = mergedUser;
        Competitor mergedCompetitor = JPA.em().merge(competitor);
        mergedSquad.competitors.add(mergedCompetitor);
        mergedTeam.competitors.add(mergedCompetitor);
        */

        return redirect(routes.Competitions.list());
    }

    @Transactional(readOnly = true)
    public Result list() {
        List<Competition> competitions = JPA.em().createQuery("SELECT c FROM Competition c", Competition.class).getResultList();
        return ok(competitionlist.render(competitions));
    }

    @Transactional(readOnly = true)
    public Result show(Long competitionId, String tab) {
        Competition competition = JPA.em().find(Competition.class, competitionId);
        session().put("competitionId", competitionId.toString());
        session().put("tab", tab);
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
        Logger.debug("requestData.id = " + requestData.id);
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
        return pdfGenerator.ok(startlistprint.render(competition.startlistEntries(), competition.label, competition.dateString(), printDate), httpRequest);
    }

    @Transactional(readOnly = true)
    public Result editStage(Long stageId) {
        Long competitionId = Long.valueOf(session().get("competitionId"));
        List<CompetitionClass> competitionClasses = JPA.em().createQuery("SELECT c FROM CompetitionClass c ORDER BY c.label", CompetitionClass.class).getResultList();
        if (stageId != 0) {
            return ok(stageedit.render(competitionId, JPA.em().find(Stage.class, stageId), competitionClasses));
        } else {
            Competition competition = JPA.em().find(Competition.class, competitionId);
            Stage stage = new Stage(competition.stages.size() + 1);
            return ok(stageedit.render(competitionId, stage, competitionClasses));
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
    public Result deleteStage(Long stageId) {
        Long competitionId = Long.valueOf(session().get("competitionId"));
        Competition competition = JPA.em().find(Competition.class, competitionId);
        competition.stages.remove(JPA.em().find(Stage.class, stageId));
        return redirect(routes.Competitions.show(competitionId, "stages"));
    }

    @Transactional(readOnly = true)
    public Result editSquad(Long squadId) {
        Long competitionId = Long.valueOf(session().get("competitionId"));
        List<CompetitionClass> competitionClasses = JPA.em().createQuery("SELECT c FROM CompetitionClass c ORDER BY c.label", CompetitionClass.class).getResultList();

        if (squadId != 0) {
            return ok(squadedit.render(JPA.em().find(Squad.class, squadId), competitionClasses));
        } else {
            Competition competition = JPA.em().find(Competition.class, competitionId);
            Squad squad = new Squad(competition.squadSize);
            return ok(squadedit.render(squad, competitionClasses));
        }
    }

    @Transactional
    public Result saveSquad() {
        DynamicForm dynamicData = formFactory.form().bindFromRequest();
        Long competitionId = Long.valueOf(session().get("competitionId"));
        Competition competition = JPA.em().find(Competition.class, competitionId);
        Form<Squad> squadForm = formFactory.form(Squad.class);
        squadForm.bindFromRequest();
        if (squadForm.hasErrors()) {
            Logger.error("errors in Squad data");
        }
        Squad requestData = formFactory.form(Squad.class).bindFromRequest().get();
        if (requestData == null) {
            Logger.error("no squad data found in request");
        }

        // the following is a completely ridiculous solution, but it's the only one I can get to work properly
        for (String key: dynamicData.data().keySet()) {
            if (key.startsWith("classIds")) {
                CompetitionClass competitionClass = JPA.em().find(CompetitionClass.class, new Long(dynamicData.data().get(key)));
                requestData.allowedClasses.add(competitionClass);
            }
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            requestData.rollcall = new DateTime(sdf.parse(dynamicData.data().get("rollcalltime")));
        } catch (ParseException pe){
            Logger.error("error parsing rollcall string");
            flash().put("error", "message.error.failedtosave");
            return redirect(routes.Competitions.show(competitionId, "squads"));
        }

        if (requestData.id != 0) {
            Squad squad = JPA.em().find(Squad.class, requestData.id);
            squad.copyValues(requestData);
        } else {
            competition.squads.add(JPA.em().merge(requestData));
        }

        return redirect(routes.Competitions.show(competitionId, "squads"));
    }

    @Transactional
    public Result deleteSquad(Long squadId) {
        Long competitionId = Long.valueOf(session().get("competitionId"));
        Competition competition = JPA.em().find(Competition.class, competitionId);
        competition.squads.remove(JPA.em().find(Squad.class, squadId));
        return redirect(routes.Competitions.show(competitionId, "squads"));
    }

    @Transactional
    public Result generateSquads() {
        DynamicForm dynamicData = formFactory.form().bindFromRequest();
        Long competitionId = Long.valueOf(session().get("competitionId"));
        String action = dynamicData.get("action");

        switch (action) {
            case "add":
                return ok(squadgenerate.render(competitionId));
            case "save":
                int squadCount = Integer.parseInt(dynamicData.get("squadCount"));
                int interval = Integer.parseInt(dynamicData.get("interval"));
                DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
                DateTime rollcall = formatter.parseDateTime(dynamicData.get("startTime"));

                Competition competition = JPA.em().find(Competition.class, competitionId);
                for (int i = 0; i < squadCount; i++) {
                    Squad squad = new Squad();
                    squad.label = Integer.toString(i + 1);
                    squad.slots = competition.squadSize;
                    squad.rollcall = rollcall;

                    competition.squads.add(JPA.em().merge(squad));
                    rollcall = rollcall.plusMinutes(interval);
                }
        }
        return redirect(routes.Competitions.show(competitionId, "squads"));
    }

    @Transactional
    public Result printSquad(Long squadId) {
        Long competitionId = Long.valueOf(session().get("competitionId"));
        Competition competition = JPA.em().find(Competition.class, competitionId);
        Squad squad = JPA.em().find(Squad.class, squadId);

        return ok(squadprint.render(squad, competition.stages));
    }

    @Transactional(readOnly = true)
    public Result addCompetitor() {
        List<User> users = JPA.em().createQuery("SELECT u FROM User u ORDER BY u.firstName, u.lastName", User.class).getResultList();

        Long competitionId = Long.valueOf(session().get("competitionId"));
        Competition competition = JPA.em().find(Competition.class, competitionId);
        ArrayList<Squad> squads = new ArrayList<>();
        for (Squad squad: competition.squads) {
            if (squad.competitors() < squad.slots) {
                squads.add(squad);
            }
        }
        List<CompetitionClass> competitionClasses = JPA.em().createQuery("SELECT c FROM CompetitionClass c ORDER BY c.label", CompetitionClass.class).getResultList();
        List<CompetitionSubclass> competitionSubclasses = JPA.em().createQuery("SELECT c FROM CompetitionSubclass c ORDER BY c.label", CompetitionSubclass.class).getResultList();

        return ok(competitoradd.render(squads, users, competitionClasses, competitionSubclasses));
    }

    @Transactional
    public Result saveCompetitor() {
        DynamicForm dynamicData = formFactory.form().bindFromRequest();
        Long competitionId = Long.valueOf(session().get("competitionId"));
        Competition competition = JPA.em().find(Competition.class, competitionId);
        Long squadId = Long.valueOf(dynamicData.get("squadId"));
        Squad squad = JPA.em().find(Squad.class, squadId);
        Long userId = Long.valueOf(dynamicData.get("userId"));
        User user = JPA.em().find(User.class, userId);

        Competitor competitor = formFactory.form(Competitor.class).bindFromRequest().get();

        try {
            competitor.competitionClass = JPA.em().find(CompetitionClass.class, Long.valueOf(dynamicData.get("competitionClassId")));
        } catch (Exception e) {
            flash().put("error", "message.error.invalidclass");
            flash().put("squadId", squadId.toString());
            flash().put("userId", userId.toString());
            return redirect(routes.Competitions.addCompetitor());
        }

        try {
            competitor.competitionSubclass = JPA.em().find(CompetitionSubclass.class, Long.valueOf(dynamicData.get("competitionSubclassId")));
        } catch (Exception e) {
            // no subclass selected, no error
        }

        if (squad.allowedClasses.size() != 0 && !squad.allowedClasses.contains(competitor.competitionClass)) {
            flash().put("error", "message.error.classnotallowed");
            flash().put("squadId", squadId.toString());
            flash().put("userId", userId.toString());
            flash().put("competitionClass", competitor.competitionClass.id.toString());
            if (competitor.competitionSubclass != null) {
                flash().put("competitionSubclass", competitor.competitionSubclass.id.toString());
            }
            return redirect(routes.Competitions.addCompetitor());
        }

        if (!ValidateBookingTime(competition, squad, user)) {
            flash().put("error", "message.error.bookingtooclose");
            flash().put("squadId", squadId.toString());
            flash().put("userId", userId.toString());
            flash().put("competitionClass", competitor.competitionClass.id.toString());
            if (competitor.competitionSubclass != null) {
                flash().put("competitionSubclass", competitor.competitionSubclass.id.toString());
            }
            return redirect(routes.Competitions.addCompetitor());
        }

        competitor.user = user;
        squad.competitors.add(JPA.em().merge(competitor));

        return redirect(routes.Competitions.show(competitionId, "competitors"));
    }

    @Transactional
    public Result moveCompetitor() {
        DynamicForm dynamicData = formFactory.form().bindFromRequest();
        Long sourceSquadId = Long.valueOf(dynamicData.get("sourceSquadId"));
        Long competitorId = Long.valueOf(dynamicData.get("competitorId"));

        if (sourceSquadId != 0 && competitorId != 0) {
            Competitor competitor = JPA.em().find(Competitor.class, competitorId);
            Squad sourceSquad = JPA.em().find(Squad.class, sourceSquadId);

            String targetSquadString = dynamicData.get("targetSquadId");
            if (targetSquadString == null || targetSquadString.isEmpty()) {
                Competition competition = JPA.em().find(Competition.class, Long.valueOf(session().get("competitionId")));
                List<Squad> squads = new ArrayList<>();
                for (Squad squad : competition.squads) {
                    if (!squad.id.equals(sourceSquadId) && (squad.allowedClasses.size() != 0 && squad.allowedClasses.contains(competitor.competitionClass)) && squad.available()) {
                        squads.add(squad);
                    }
                }

                if (targetSquadString != null && targetSquadString.isEmpty()) {
                    flash().put("error", "message.error.invalidinput");
                }
                return ok(competitormove.render(sourceSquad, competitor, squads));
            }

            Long targetSquadId = Long.valueOf(targetSquadString);
            if (targetSquadId != 0) {
                Squad targetSquad = JPA.em().find(Squad.class, targetSquadId);

                Competition competition = JPA.em().find(Competition.class, Long.valueOf(session().get("competitionId")));
                if (!ValidateBookingTime(competition, targetSquad, competitor.user, sourceSquad)) {
                    List<Squad> squads = new ArrayList<>();
                    for (Squad squad : competition.squads) {
                        if (!squad.id.equals(sourceSquadId) && (squad.allowedClasses.size() != 0 && squad.allowedClasses.contains(competitor.competitionClass))) {
                            squads.add(squad);
                        }
                    }

                    flash().put("targetSquadId", targetSquadId.toString());
                    flash().put("error", "message.error.bookingtooclose");
                    return ok(competitormove.render(sourceSquad, competitor, squads));
                }

                if (sourceSquad.competitors.remove(competitor)) {
                    targetSquad.competitors.add(competitor);
                }
            }
        }

        return redirect(routes.Competitions.show(Long.valueOf(session().get("competitionId")), "competitors"));
    }

    @Transactional
    public Result removeCompetitor(Long squadId, Long competitorId) {
        Squad squad = JPA.em().find(Squad.class, squadId);
        Competitor competitor = JPA.em().find(Competitor.class, competitorId);
        squad.competitors.remove(competitor);

        return redirect(routes.Competitions.show(Long.valueOf(session().get("competitionId")), "competitors"));
    }

    @Transactional(readOnly = true)
    public Result editTeam(Long teamId) {
        Long competitionId = Long.valueOf(session().get("competitionId"));
        List<CompetitionClass> competitionClasses = JPA.em().createQuery("SELECT c FROM CompetitionClass c ORDER BY c.label", CompetitionClass.class).getResultList();

        if (teamId != 0) {
            return ok(teamedit.render(JPA.em().find(Team.class, teamId), competitionClasses));
        } else {
            Competition competition = JPA.em().find(Competition.class, competitionId);
            Team team = new Team();
            return ok(teamedit.render(team, competitionClasses));
        }
    }

    @Transactional
    public Result saveTeam() {
        DynamicForm dynamicData = formFactory.form().bindFromRequest();
        Long competitionId = Long.valueOf(session().get("competitionId"));
        Competition competition = JPA.em().find(Competition.class, competitionId);
        Team requestData = formFactory.form(Team.class).bindFromRequest().get();
        Team team = JPA.em().find(Team.class, requestData.id);

        // the following is a completely ridiculous solution, but it's the only one I can get to work properly
        for (String key: dynamicData.data().keySet()) {
            if (key.startsWith("classIds")) {
                CompetitionClass competitionClass = JPA.em().find(CompetitionClass.class, new Long(dynamicData.data().get(key)));
                requestData.allowedClasses.add(competitionClass);
            }
        }

        if (team != null) {
            team.copyValues(requestData);
        } else {
            competition.teams.add(JPA.em().merge(requestData));
        }

        return redirect(routes.Competitions.show(competitionId, "teams"));
    }

    @Transactional
    public Result deleteTeam(Long teamId) {
        Long competitionId = Long.valueOf(session().get("competitionId"));
        Competition competition = JPA.em().find(Competition.class, competitionId);
        Team team = JPA.em().find(Team.class, teamId);

        if (competition != null && team != null) {
            competition.teams.remove(team);
        }

        return redirect(routes.Competitions.show(competitionId, "teams"));
    }

    @Transactional
    public Result addCompetitorToTeam(Long teamId, Long competitorId) {
        Long competitionId = Long.valueOf(session().get("competitionId"));

        if (teamId != 0) {
            Team team = JPA.em().find(Team.class, teamId);
            if (competitorId != 0) {
                Competitor competitor = JPA.em().find(Competitor.class, competitorId);
                if (team != null && competitor != null) {
                    if (team.allowedClasses.size() != 0 && !team.allowedClasses.contains(competitor.competitionClass)) {
                        flash().put("error", "message.error.classnotallowed");
                        return redirect(routes.Competitions.addCompetitorToTeam(teamId, 0));
                    }

                    team.competitors.add(competitor);
                }
            } else {
                Competition competition = JPA.em().find(Competition.class, competitionId);
                List<Competitor> competitors = new ArrayList<>(competition.allCompetitors());
                competitors.removeAll(team.competitors);
                if (competitors.size() != 0) {
                    Collections.sort(competitors);
                    return ok(competitorselection.render(teamId, competitors));
                } else {
                    flash().put("error", "message.error.noavailablecompetitors");
                }
            }
        }

        return redirect(routes.Competitions.show(competitionId, "teams"));
    }

    @Transactional
    public Result removeCompetitorFromTeam(Long teamId, Long competitorId) {
        Long competitionId = Long.valueOf(session().get("competitionId"));

        Team team = JPA.em().find(Team.class, teamId);
        Competitor competitor = JPA.em().find(Competitor.class, competitorId);
        if (team != null && competitor != null) {
            team.competitors.remove(competitor);
        }

        return redirect(routes.Competitions.show(competitionId, "teams"));
    }

    @Transactional()
    public Result compactSquads(Long competitionId) {
        ArrayList<Squad> squads = (ArrayList<Squad>)JPA.em().createQuery("SELECT s FROM Squad s ORDER BY rollcall", Squad.class).getResultList();

        Integer i = 0;
        for(Squad squad: squads) {
            if (squad.rollcall != null) {
                squad.rollcall = squad.rollcall.minusMinutes(i++);
            }
        }

        return redirect(routes.Competitions.show(competitionId, "squads"));
    }

    @Transactional()
    public Result expandSquads(Long competitionId) {
        ArrayList<Squad> squads = (ArrayList<Squad>)JPA.em().createQuery("SELECT s FROM Squad s ORDER BY rollcall", Squad.class).getResultList();

        Integer i = 0;
        for(Squad squad: squads) {
            if (squad.rollcall != null) {
                squad.rollcall = squad.rollcall.plusMinutes(i++);
            }
        }

        return redirect(routes.Competitions.show(competitionId, "squads"));
    }

    @Transactional(readOnly = true)
    public Result export(Long competitionId) {
        Competition competition = JPA.em().find(Competition.class, competitionId);

        return ok(Json.toJson(competition));
    }
}
