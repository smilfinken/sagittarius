package controllers;

import java.util.List;
import models.Competition;
import models.Division;
import models.User;
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
}
