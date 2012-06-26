package controllers;

import java.util.List;
import models.Competition;
import models.Competitor;
import models.Squad;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Squads extends Controller {

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
}
