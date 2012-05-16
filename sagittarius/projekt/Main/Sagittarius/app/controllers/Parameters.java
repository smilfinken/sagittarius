package controllers;

import java.util.List;
import models.Category;
import models.CompetitionType;
import models.Division;
import models.Rank;
import models.VerifiedUser;
import models.*;
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
		List<Stage> stages = Stage.all().fetch();
		List<TargetGroup> targetGroups = TargetGroup.all().fetch();
		List<Target> targets = Target.all().fetch();
		List<Users> users = VerifiedUser.all().fetch();

		render(categories, ranks, competitionTypes, divisions, users);
	}
}