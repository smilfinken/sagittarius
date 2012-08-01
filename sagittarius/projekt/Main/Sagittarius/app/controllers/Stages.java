package controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Stages extends Controller {

	public static void details(long competitionID, long stageID) {
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
			render(competition, stage, targetShapes, targetColours, startingPositions, minTime, maxTime);
		} catch (Exception e) {
			render(competition, stage, targetShapes, targetColours, startingPositions);
		}
	}

	//TODO: refactor - move the external user actions (ie TargetGroup, Target) into the respective controller
	@Check("admin")
	public static void edit(long competitionID, long stageID, String label, long startingPositionID, String useraction) {
		Competition competition = Competition.findById(competitionID);
		Stage stage = Stage.findById(stageID);
		StartingPosition startingPosition = StartingPosition.findById(startingPositionID);

		if (stage != null) {
			if (params._contains("useraction")) {
				switch (useraction) {
					case "save":
						stage.label = label;
						stage.startingPosition = startingPosition;
						stage.save();
						break;
					case "delete":
						if (competition != null) {
							competition.deleteStage(stage);
						}
						Competitions.details(competitionID);
						break;
				}
			}
		}

		details(competitionID, stageID);
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

		details(competitionID, stageID);
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

		details(competitionID, stageID);
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

		details(competitionID, stageID);
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

		details(competitionID, stageID);
	}

	public static void listAsXML(long competitionID) {
		Document document = DocumentHelper.createDocument();
		Competition competition = Competition.findById(competitionID);
		if (competition != null) {
			List<Stage> stages = competition.stages;
			Element root = document.addElement("data").addElement(Stages.class.getSimpleName().toLowerCase());
			for (Stage stage : stages) {
				Element element = root.addElement(stage.getClass().getSimpleName().toLowerCase());
				element.addAttribute("stageindex", String.format("%d", stage.stageIndex));
				element.addAttribute("label", stage.label);
				element.addAttribute("targetcount", String.format("%d", stage.targetCount()));
				element.addAttribute("haspoints", String.format("%b", stage.hasPoints()));
			}
		}

		try {
			File exportFile = File.createTempFile("sgt", "xml");
			exportFile.deleteOnExit();

			FileWriter exportWriter = new FileWriter(exportFile);
			document.write(exportWriter);
			exportWriter.write('\n');
			exportWriter.close();

			response.setHeader("Content-Length", String.format("%d", exportFile.length()));
			response.setHeader("Content-Type", "text/xml; charset=utf-8");
			renderBinary(exportFile);
		} catch (IOException ex) {
			Logger.getLogger(Competitions.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}