package controllers;

import java.util.List;
import models.Category;
import models.CompetitionType;
import models.Division;
import models.Rank;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Parameters extends Controller {

	public static void list() {
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		List<Division> divisions = Division.all().fetch();
		List<Users> users = User.all().fetch();

		render(categories, ranks, competitionTypes, divisions, users);
	}
}