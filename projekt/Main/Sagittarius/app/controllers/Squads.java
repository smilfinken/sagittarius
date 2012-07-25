package controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Competition;
import models.Competitor;
import models.Squad;
import models.User;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Squads extends Controller {

	public static void list(long competitionID) {
		Competition competition = Competition.findById(competitionID);

		User user = User.find("byEmail", session.get("username")).first();
		List<Competitor> competitors = new ArrayList<>();
		for (Iterator<Object> it = Competitor.find("byUser", user).fetch().iterator(); it.hasNext();) {
			Competitor competitor = (Competitor) it.next();
			if (competition.competitors.contains(competitor)) {
				competitors.add(competitor);
			}
		}

		render(competition, competitors);
	}

	public static void details(long competitionID, long squadID) {
		Competition competition = Competition.findById(competitionID);

		List<Competitor> competitors = competition.competitors;
		for (Squad squad : competition.squads) {
			competitors.removeAll(squad.competitors);
		}

		Squad squad = Squad.findById(squadID);
		render(competition, squad, competitors);
	}

	@Check("admin")
	public static void edit(long competitionID, long squadID, long competitorID, String label, int slots, String useraction) {
		Competition competition = Competition.findById(competitionID);
		Squad squad = Squad.findById(squadID);
		Competitor competitor = Competitor.findById(competitorID);

		if (squad != null) {
			if (params._contains("useraction")) {
				switch (useraction) {
					case "save":
						squad.label = label;
						if (slots > 0) {
							squad.slots = slots;
						} else {
							squad.slots = -1;
						}
						squad.save();
						break;
					case "delete":
						if (competition != null) {
							competition.deleteSquad(squad);
						}
						Competitions.details(competitionID);
						break;
					case "addcompetitor":
						if (squad != null && competitor != null) {
							squad.addCompetitor(competitor);
						}
						break;
					case "removecompetitor":
						if (squad != null && competitor != null) {
							squad.removeCompetitor(competitor);
							break;
						}
				}
			}
		}

		details(competitionID, squadID);
	}

	public static void register(long competitionID, long squadID, long competitorID) {
		Squad squad = Squad.findById(squadID);
		Competitor competitor = Competitor.findById(competitorID);

		if (squad != null && competitor != null && !squad.competitors.contains(competitor)) {
			squad.addCompetitor(competitor);
		}

		list(competitionID);
	}

	public static void listAsXML(long competitionID) {
		Document document = DocumentHelper.createDocument();
		Competition competition = Competition.findById(competitionID);
		if (competition != null) {
			List<Squad> squads = competition.squads;
			Element root = document.addElement("root").addElement(Squads.class.getSimpleName().toLowerCase());
			for (Squad squad : squads) {
				Element element = root.addElement(squad.getClass().getSimpleName().toLowerCase());
				element.addAttribute("id", String.format("%d", squad.id));
				element.addAttribute("label", squad.label);
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