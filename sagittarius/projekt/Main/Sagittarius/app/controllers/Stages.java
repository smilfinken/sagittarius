package controllers;

import java.util.List;
import models.Competition;
import models.Stage;
import models.Target;
import models.TargetGroup;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Stages extends Controller {

	public static void edit(long competitionID, long stageID, int stageIndex) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);
		render(competition, stage, stageIndex);
	}

	public static void update(long competitionID, long stageID, int stageIndex, long targetGroupID, String name) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);
		if (stage != null) {
			stage.willBeSaved = true;
			stage.name = name;
		}
		renderTemplate("Stages/edit.html", competition, stage, stageIndex);
	}

	public static void addTargetGroup(long competitionID, long stageID, int stageIndex) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);

		renderTemplate("Stages/edit.html", competition, stage, stageIndex);
	}

	public static void updateTargetGroup(long competitionID, long stageID, int stageIndex, long targetGroupID, List<Target> targets) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);

		TargetGroup targetGroup = TargetGroup.findById(targetGroupID);
		if (targetGroup != null) {
			for (Target t : targets) {
				Target target = Target.findById(t.id);
				if (target != null) {
					target.willBeSaved = true;
					target.hasPoints = t.hasPoints;
				}
			}
		}

		renderTemplate("Stages/edit.html", competition, stage, stageIndex);
	}

	public static void deleteTargetGroup(long competitionID, long stageID, long targetGroupID, int stageIndex) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);

		TargetGroup targetGroup = TargetGroup.findById(targetGroupID);

		if (stage != null && targetGroup != null) {
			stage.willBeSaved = true;
			targetGroup.willBeSaved = true;
			for (Target target : targetGroup.targets) {
				targetGroup.targets.remove(target);
				target.delete();
			}
			stage.targetGroups.remove(targetGroup);
			targetGroup.delete();
		}

		renderTemplate("Stages/edit.html", competition, stage, stageIndex);
	}
}