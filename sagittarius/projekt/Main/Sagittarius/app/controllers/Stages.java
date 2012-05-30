package controllers;

import java.util.ArrayList;
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
public class Stages extends Controller {

	public static void edit(long competitionID, long stageID) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);
		List<TargetModel> targetModels = TargetModel.all().fetch();

		renderTemplate("Stages/edit.html", competition, stage, targetModels);
	}

	public static void update(long competitionID, long stageID, String name) {
		Stage stage = Stage.findById(stageID);

		if (stage != null) {
			stage.name = name;
			stage.save();
		}

		edit(competitionID, stageID);
	}

	public static void addTargetGroup(long competitionID, long stageID) {
		Stage stage = Stage.findById(stageID);

		if (stage != null) {
			TargetGroup targetGroup = new TargetGroup("Målgrupp", new ArrayList<Target>());
			targetGroup.save();
			stage.targetGroups.add(targetGroup);
			stage.save();
		}

		edit(competitionID, stageID);
	}

	public static void deleteTargetGroup(long competitionID, long stageID, long targetGroupID) {
		Stage stage = Stage.findById(stageID);
		TargetGroup targetGroup = TargetGroup.findById(targetGroupID);

		// TODO: fix this so that data is properly removed from db on deletion
		if (stage != null && targetGroup != null) {
			targetGroup.targets = null;
			targetGroup.save();
			stage.targetGroups.remove(targetGroup);
			stage.save();
		}

		edit(competitionID, stageID);
	}

	public static void addTarget(long competitionID, long stageID, long targetGroupID, String hasPoints) {
		TargetGroup targetGroup = TargetGroup.findById(targetGroupID);

		if (targetGroup != null) {
			Target target = new Target();
			target.hasPoints = (hasPoints != null);
			target.targetModel = null;
			target.save();
			targetGroup.targets.add(target);
			targetGroup.save();
		}

		edit(competitionID, stageID);
	}

	public static void updateTarget(long competitionID, long stageID, long targetID, String hasPoints, long modelID) {
		Target target = Target.findById(targetID);

		if (target != null) {
			target.willBeSaved = true;
			target.hasPoints = (hasPoints != null);
			target.targetModel = TargetModel.findById(modelID);
		}

		edit(competitionID, stageID);
	}

	public static void deleteTarget(long competitionID, long stageID, long targetGroupID, long targetID, String hasPoints, String model) {
		TargetGroup targetGroup = TargetGroup.findById(targetGroupID);
		Target target = Target.findById(targetID);

		if (targetGroup != null && target != null) {
			targetGroup.targets.remove(target);
			targetGroup.save();
			target.delete();
		}

		edit(competitionID, stageID);
	}
}