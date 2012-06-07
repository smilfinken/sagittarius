package controllers;

import static common.Sorting.sortCompetitors;
import static common.Sorting.sortUsers;
import java.util.List;
import models.*;
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

							List<Competitor> competitors = competition.competitors;
							List<User> users = User.all().fetch();
							List<Category> categories = Category.all().fetch();
							List<Rank> ranks = Rank.all().fetch();
							List<Division> divisions = Division.all().fetch();
							renderTemplate("Competitions/competitors.html", competition, sortCompetitors(competitors), sortUsers(users), categories, ranks, divisions);
						}
						break;
				}
			}
		}

		flash.put("divisionID", competitor.division.id);
		List<Division> divisions = Division.all().fetch();
		render(competition, competitor, divisions);
	}
}