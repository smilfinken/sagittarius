package controllers;

import java.util.List;
import models.Category;
import models.CompetitionType;
import models.Rank;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Parameters extends Controller {

	public static void list() {
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		render(categories, ranks, competitionTypes);
	}
}