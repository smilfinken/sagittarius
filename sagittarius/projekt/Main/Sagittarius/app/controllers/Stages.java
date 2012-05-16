package controllers;

import java.util.List;
import models.Competition;
import models.Stage;
import models.Target;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Stages extends Controller {

	public static void list(int competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Stage> stages = competition.stages;
		render(stages);
	}

	public static void deleteTargetGroup(long competitionID, long stageID, long targetGroupID, int stageIndex) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);
		List<Target> targets = Target.all().fetch();
		renderTemplate("competitions/editstage.html",competition, stage, targets, stageIndex);
	}
}