package controllers;

import java.util.ArrayList;
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

	public static void update(long competitionID, long stageID, int stageIndex, String name) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);

		if (stage != null) {
			stage.name = name;
			stage.save();
		}

		renderTemplate("Stages/edit.html", competition, stage, stageIndex);
	}

	public static void addTargetGroup(long competitionID, long stageID, int stageIndex) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);

		if (stage != null) {
			TargetGroup targetGroup = new TargetGroup("Målgrupp", new ArrayList<Target>());
			targetGroup.save();
			stage.targetGroups.add(targetGroup);
			stage.save();
		}

		renderTemplate("Stages/edit.html", competition, stage, stageIndex);
	}

	public static void deleteTargetGroup(long competitionID, long stageID, long targetGroupID, int stageIndex) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);
		TargetGroup targetGroup = TargetGroup.findById(targetGroupID);

		// TODO: fix this so that data is properly removed from db on deletion
		if (stage != null && targetGroup != null) {
			targetGroup.targets = null;
			targetGroup.save();
			stage.targetGroups.remove(targetGroup);
			stage.save();
		}

		renderTemplate("Stages/edit.html", competition, stage, stageIndex);
	}

	public static void addTarget(long competitionID, long stageID, long stageIndex, long targetGroupID, String hasPoints) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);
		TargetGroup targetGroup = TargetGroup.findById(targetGroupID);

		if (targetGroup != null) {
			Target target = new Target();
			target.hasPoints = (hasPoints != null);
			target.save();
			targetGroup.targets.add(target);
			targetGroup.save();
		}

		renderTemplate("Stages/edit.html", competition, stage, stageIndex);
	}

	public static void updateTarget(long competitionID, long stageID, int stageIndex, long targetID, String hasPoints, String model) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);
		Target target = Target.findById(targetID);

		if (target != null) {
			target.willBeSaved = true;
			target.hasPoints = (hasPoints != null);
			target.model = model;
		}

		renderTemplate("Stages/edit.html", competition, stage, stageIndex);
	}

	public static void deleteTarget(long competitionID, long stageID, int stageIndex, long targetGroupID, long targetID, String hasPoints, String model) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);
		TargetGroup targetGroup = TargetGroup.findById(targetGroupID);
		Target target = Target.findById(targetID);

		if (targetGroup != null && target != null) {
			targetGroup.targets.remove(target);
			targetGroup.save();
			target.delete();
		}

		renderTemplate("Stages/edit.html", competition, stage, stageIndex);
	}
}