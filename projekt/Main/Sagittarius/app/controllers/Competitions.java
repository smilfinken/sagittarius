package controllers;

import java.util.List;
import models.Competition;
import models.CompetitionType;
import models.Competitor;
import models.Stage;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Competitions extends Controller {

	public static void select(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		render(competition);
	}

	public static void add(String name, long competitionTypeID) {
		Competition competition = new Competition(name);
		competition.willBeSaved = true;
		competition.competitionType = CompetitionType.findById(competitionTypeID);
		render(competition);
	}

	public static void edit(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Stage> stages = competition.stages;
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		render(competition, stages, competitionTypes);
	}

	public static void save(long competitionID, String name) {
		Competition competition = Competition.findById(competitionID);
		competition.willBeSaved = true;
		competition.name = name;

		List<Stage> stages = competition.stages;
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		render(competition, stages, competitionTypes);
	}

	public static void list() {
		List<Competition> competitions = Competition.all().fetch();
		render(competitions);
	}

	public static void competitors(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Competitor> competitors = competition.competitors;
		render(competition, competitors);
	}

	public static void enroll(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		render(competition);
	}

	public static void addStage(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Stage> stages = competition.stages;
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		render(competition, stages, competitionTypes);
	}

	public static void editStage(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Stage> stages = competition.stages;
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		render(competition, stages, competitionTypes);
	}

	public static void deleteStage(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Stage> stages = competition.stages;
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		render(competition, stages, competitionTypes);
	}
}