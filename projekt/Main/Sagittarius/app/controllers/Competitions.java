package controllers;

import java.text.DateFormat;
import java.util.List;
import models.*;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Competitions extends Controller {

	public static void list() {
		List<Competition> competitions = Competition.all().fetch();

		render(competitions);
	}

	public static void select(long competitionID) {
		Competition competition = Competition.findById(competitionID);

		render(competition);
	}

	public static void add(String name, long competitionTypeID, String date) {
		Competition competition = new Competition(name);

		if (date != null) {
			try {
				DateFormat shortDate = DateFormat.getDateInstance(DateFormat.SHORT);
				competition.date = shortDate.parse(date);
			} catch (Exception e) {
			}
		}
		competition.competitionType = CompetitionType.findById(competitionTypeID);
		competition.save();

		renderTemplate("Competitions/select.html", competition);
	}

	public static void edit(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Stage> stages = competition.stages;
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();

		render(competition, stages, competitionTypes);
	}

	public static void save(long competitionID, String name, String date) {
		Competition competition = Competition.findById(competitionID);

		if (competition != null) {
			if (name.length() > 0) {
				competition.name = name;
			}
			if (date.length() > 0) {
				try {
					DateFormat shortDate = DateFormat.getDateInstance(DateFormat.SHORT);
					competition.date = shortDate.parse(date);
				} catch (Exception e) {
				}
			}
			competition.save();
		}

		List<Stage> stages = competition.stages;
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();

		renderTemplate("Competitions/edit.html", competition, stages, competitionTypes);
	}

	// TODO: fix this so that data is properly removed from db on deletion
	public static void delete(long competitionID) {
		Competition competition = Competition.findById(competitionID);

		if (competition != null) {
			competition.competitors = null;
			competition.stages = null;
			competition.delete();
		}

		renderTemplate("Application/index.html");
	}

	public static void competitors(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Competitor> competitors = competition.competitors;
		List<User> users = User.all().fetch();

		render(competition, competitors, users);
	}

	public static void enroll(long competitionID) {
		Competition competition = Competition.findById(competitionID);

		renderTemplate("Competitions/select.html", competition);
	}

	public static void addStage(long competitionID, String name) {
		Competition competition = Competition.findById(competitionID);

		if (competition != null) {
			Stage stage = new Stage(1, name);
			stage.save();
			competition.stages.add(stage);
			competition.save();
		}

		List<Stage> stages = competition.stages;
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();

		renderTemplate("Competitions/edit.html", competition, stages, competitionTypes);
	}

	public static void deleteStage(long competitionID, long stageID) {
		Competition competition = Competition.findById(competitionID);

		// TODO: fix this so that data is properly removed from db on deletion
		Stage stage = Stage.findById(stageID);
		if (stage != null) {
			for (TargetGroup targetGroup : stage.targetGroups) {
				targetGroup.targets = null;
				targetGroup.save();
			}
			stage.targetGroups = null;
			stage.save();
			competition.stages.remove(stage);
			competition.save();
			stage.delete();
		}

		List<Stage> stages = competition.stages;
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();

		renderTemplate("Competitions/edit.html", competition, stages, competitionTypes);
	}

	public static void registerUser(long competitionID, long userID, long divisionID) {
		Competition competition = Competition.findById(competitionID);
		User user = User.findById(userID);
		Division division = Division.findById(divisionID);

		if (user != null && division != null) {
			Competitor competitor = new Competitor(user, division);
			competitor.save();
			competition.competitors.add(competitor);
			competition.save();
		}

		List<Competitor> competitors = competition.competitors;
		List<User> users = User.all().fetch();

		renderTemplate("Competitions/competitors.html", competition, competitors, users);
	}

	public static void unregisterUser(long competitionID, long competitorID) {
		Competition competition = Competition.findById(competitionID);
		Competitor competitor = Competitor.findById(competitorID);

		// TODO: fix this so that data is properly removed from db on deletion
		if (competitor != null) {
			competition.competitors.remove(competitor);
			competition.save();
			competitor.results = null;
			competitor.delete();
		}

		List<Competitor> competitors = competition.competitors;
		List<User> users = User.all().fetch();

		renderTemplate("Competitions/competitors.html", competition, competitors, users);
	}

	public static void newCompetitor(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Competitor> competitors = competition.competitors;
		List<User> users = User.all().fetch();

		renderTemplate("Competitions/competitors.html", competition, competitors, users);
	}
}