package controllers;

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
public class Stages extends Controller {

	@Check("admin")
	public static void edit(long competitionID, long stageID) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);
		List<TargetShape> targetShapes = TargetShape.all().fetch();
		List<TargetColour> targetColours = TargetColour.all().fetch();

		renderTemplate("Stages/edit.html", competition, stage, targetShapes, targetColours);
	}

	@Check("admin")
	public static void update(long competitionID, long stageID, String name) {
		Stage stage = Stage.findById(stageID);

		if (stage != null) {
			stage.label = name;
			stage.save();
		}

		edit(competitionID, stageID);
	}

	@Check("admin")
	public static void addTargetGroup(long competitionID, long stageID, int range) {
		Stage stage = Stage.findById(stageID);

		if (stage != null) {
			TargetGroup targetGroup = new TargetGroup("Målgrupp", range, new ArrayList<Target>());
			targetGroup.save();
			stage.targetGroups.add(targetGroup);
			stage.save();
		}

		edit(competitionID, stageID);
	}

	@Check("admin")
	public static void editTargetGroup(long competitionID, long stageID, long targetGroupID, int range, String useraction) {
		Stage stage = Stage.findById(stageID);
		TargetGroup targetGroup = TargetGroup.findById(targetGroupID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					if (targetGroup != null) {
						targetGroup.range = range;
						targetGroup.save();
					}
					break;
				case "delete":
					// TODO: fix this so that data is properly removed from db on deletion
					if (stage != null && targetGroup != null) {
						targetGroup.targets = null;
						targetGroup.save();
						stage.targetGroups.remove(targetGroup);
						stage.save();
					}
					break;
			}

		}

		edit(competitionID, stageID);
	}

	@Check("admin")
	public static void addTarget(long competitionID, long stageID, long targetGroupID, long shapeID, long colourID, boolean hasPoints) {
		TargetGroup targetGroup = TargetGroup.findById(targetGroupID);

		if (targetGroup != null) {
			TargetShape targetShape = TargetShape.findById(shapeID);
			TargetColour targetColour = TargetColour.findById(colourID);
			Target target = new Target(targetShape, targetColour, hasPoints);
			target.save();
			targetGroup.targets.add(target);
			targetGroup.save();
		}

		edit(competitionID, stageID);
	}

	@Check("admin")
	public static void updateTarget(long competitionID, long stageID, long targetID, String hasPoints, long shapeID, long colourID) {
		Target target = Target.findById(targetID);

		if (target != null) {
			target.willBeSaved = true;
			target.hasPoints = (hasPoints != null);
			target.targetShape = TargetShape.findById(shapeID);
			target.targetColour = TargetColour.findById(colourID);
		}

		edit(competitionID, stageID);
	}

	@Check("admin")
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