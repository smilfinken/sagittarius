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
		List<StartingPosition> startingPositions = StartingPosition.all().fetch();
		if (stage.startingPosition != null) {
			flash.put("startingPositionID", stage.startingPosition.id);
		}
		try {
			double timings[] = common.Timings.getExtremes(stageID, 1);
			String minTime = String.format("%.1f", timings[0]);
			String maxTime = String.format("%.1f", timings[1]);
			renderTemplate("Stages/edit.html", competition, stage, targetShapes, targetColours, startingPositions, minTime, maxTime);
		} catch (Exception e) {
			renderTemplate("Stages/edit.html", competition, stage, targetShapes, targetColours, startingPositions);
		}
	}

	@Check("admin")
	public static void update(long competitionID, long stageID, String label, long startingPositionID) {
		Stage stage = Stage.findById(stageID);
		StartingPosition startingPosition = StartingPosition.findById(startingPositionID);

		if (stage != null) {
			stage.label = label;
			stage.startingPosition = startingPosition;
			stage.save();
		}

		edit(competitionID, stageID);
	}

	@Check("admin")
	public static void addTargetGroup(long competitionID, long stageID, String label, int range) {
		Stage stage = Stage.findById(stageID);

		if (stage != null) {
			TargetGroup targetGroup = new TargetGroup(label, range, new ArrayList<Target>());
			targetGroup.save();
			stage.targetGroups.add(targetGroup);
			stage.save();
		}

		edit(competitionID, stageID);
	}

	@Check("admin")
	public static void editTargetGroup(long competitionID, long stageID, long targetGroupID, String label, int range, String useraction) {
		Stage stage = Stage.findById(stageID);
		TargetGroup targetGroup = TargetGroup.findById(targetGroupID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					if (targetGroup != null) {
						targetGroup.label = label;
						targetGroup.range = range;
						targetGroup.save();
					}
					break;
				case "delete":
					if (stage != null && targetGroup != null) {
						stage.deleteTargetGroup(targetGroup.id);
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
	public static void editTarget(long competitionID, long stageID, long targetGroupID, long targetID, boolean hasPoints, long shapeID, long colourID, String useraction) {
		TargetGroup targetGroup = TargetGroup.findById(targetGroupID);
		Target target = Target.findById(targetID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					if (target != null) {
						target.hasPoints = hasPoints;
						target.targetShape = TargetShape.findById(shapeID);
						target.targetColour = TargetColour.findById(colourID);
						target.save();
					}
					break;
				case "delete":
					if (target != null && targetGroup != null) {
						targetGroup.deleteTarget(target);
					}
					break;
			}
		}

		edit(competitionID, stageID);
	}
}