package controllers;

import java.util.List;
import models.*;
import play.db.jpa.GenericModel;
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

	public static void addStage(long competitionID, String name) {
		Competition competition = Competition.findById(competitionID);
		competition.willBeSaved = true;
		Stage stage = new Stage(1, name);
		stage.willBeSaved = true;
		List<Stage> stages = competition.stages;
		stages.add(stage);
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		render(competition, stages, competitionTypes);
	}

	public static void editStage(long competitionID, long stageID, int stageIndex) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);
		List<Target> targets = Target.all().fetch();
		render(competition, stage, targets, stageIndex);
	}

	public static void updateStage(long competitionID, long stageID, int stageIndex, long targetGroupID) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);
		List<Target> targets = Target.all().fetch();
		render(competition, stage, targets, stageIndex);
	}

	public static void deleteStage(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Stage> stages = competition.stages;
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		render(competition, stages, competitionTypes);
	}
}