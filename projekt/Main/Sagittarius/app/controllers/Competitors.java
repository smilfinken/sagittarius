package controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import models.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class Competitors extends Controller {

	public static void registration(long competitionID, long userID) {
		Competition competition = Competition.findById(competitionID);
		List<User> users = User.all().fetch();
		List<Division> divisions = Division.all().fetch();

		render(competition, userID, users, divisions);
	}

	public static void edit(long competitionID, long competitorID, long divisionID, String useraction) {
		Competition competition = Competition.findById(competitionID);
		Competitor competitor = Competitor.findById(competitorID);

		if (competitor != null) {
			if (params._contains("useraction")) {
				switch (useraction) {
					case "save":
						Division division = Division.findById(divisionID);
						if (division != null) {
							competitor.division = division;
							competitor.save();

							Competitions.competitors(competitionID);
						}
						break;
					case "delete":
						competition.unrollCompetitor(competitor);
						Competitions.competitors(competitionID);
						break;
				}
			}
		}

		if (competitor.division != null) {
			flash.put("divisionID", competitor.division.id);
		}
		Set<Division> divisions = competitor.user.getValidDivisions();
		render(competition, competitor, divisions);
	}

	public static void listAsXML(long squadID) {
		Document document = DocumentHelper.createDocument();
		Squad squad = Squad.findById(squadID);
		List<Competitor> competitors = squad.competitors;
		Element root = document.addElement("data").addElement(Competitors.class.getSimpleName().toLowerCase());
		for (Competitor competitor : competitors) {
			Element element = root.addElement(competitor.getClass().getSimpleName().toLowerCase());
			element.addAttribute("id", String.format("%d", competitor.id));
			//element.addAttribute("position", String.format("%d", competitor.squadIndex));
			element.addAttribute("name", competitor.toString());
		}

		try {
			File exportFile = File.createTempFile("sgt", "xml");
			exportFile.deleteOnExit();

			try (FileWriter exportWriter = new FileWriter(exportFile)) {
				document.write(exportWriter);
				exportWriter.write('\n');
			}

			response.setHeader("Content-Length", String.format("%d", exportFile.length()));
			response.setHeader("Content-Type", "text/xml; charset=utf-8");
			renderBinary(exportFile);
		} catch (IOException ex) {
			Logger.getLogger(Competitions.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
