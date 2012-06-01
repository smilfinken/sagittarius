package controllers;

import java.text.DateFormat;
import java.util.ArrayList;
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

	@Check("admin")
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

		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		renderTemplate("Competitions/edit.html", competition, competitionTypes);
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

		List<Competition> competitions = Competition.all().fetch();
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		renderTemplate("Application/index.html", competitions, competitionTypes);
	}

	public static void competitors(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Competitor> competitors = competition.competitors;
		List<User> users = User.all().fetch();
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		List<Division> divisions = Division.all().fetch();

		render(competition, common.Sorting.sortCompetitors(competitors), common.Sorting.sortUsers(users), categories, ranks, divisions);
	}

	public static void enroll(long competitionID) {
		Results.list(competitionID);
	}

	public static void addStage(long competitionID, String name) {
		Competition competition = Competition.findById(competitionID);

		if (competition != null) {
			Stage stage = new Stage(1, name, competition.stages.size() + 1);
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
		// TODO: fix index when deleting a stage
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

		List<User> users = User.all().fetch();
		List<Competitor> competitors = competition.competitors;
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		List<Division> divisions = Division.all().fetch();
		renderTemplate("Competitions/competitors.html", competition, common.Sorting.sortUsers(users), common.Sorting.sortCompetitors(competitors), categories, ranks, divisions, userID);
	}

	public static void unregisterUser(long competitionID, long competitorID) {
		Competition competition = Competition.findById(competitionID);
		Competitor competitor = Competitor.findById(competitorID);

		// TODO: fix this so that data is properly removed from db on deletion
		if (competition != null && competitor != null) {
			competition.competitors.remove(competitor);
			competition.save();
			competitor.results = null;
			competitor.delete();
		}

		List<User> users = User.all().fetch();
		List<Competitor> competitors = competition.competitors;
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		List<Division> divisions = Division.all().fetch();
		renderTemplate("Competitions/competitors.html", competition, common.Sorting.sortUsers(users), common.Sorting.sortCompetitors(competitors), categories, ranks, divisions);
	}

	public static void newCompetitor(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Competitor> competitors = competition.competitors;
		List<User> users = User.all().fetch();

		renderTemplate("Competitions/competitors.html", competition, common.Sorting.sortCompetitors(competitors), common.Sorting.sortUsers(users));
	}

	public static void theater(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Competitor> allcompetitors = competition.competitors;

		List<Competitor> competitors = new ArrayList<>();
		List<Competitor> results = new ArrayList<>();
		for (Competitor competitor : allcompetitors) {
			if (!competitor.isScored()) {
				competitors.add(competitor);
			} else {
				results.add(competitor);
			}
		}		
		render(competition, common.Sorting.sortResults(results), common.Sorting.sortCompetitors(competitors));
	}
	
}