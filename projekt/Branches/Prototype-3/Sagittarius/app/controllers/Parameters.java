package controllers;

import java.util.List;
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
		List<TargetModel> targetModels = TargetModel.all().fetch();
		List<Target> targets = Target.all().fetch();
		List<Competition> competitions = Competition.all().fetch();
		List<Squad> squads = Squad.all().fetch();
		List<Competitor> competitors = Competitor.all().fetch();
		List<Users> users = User.all().fetch();

		render(categories, ranks, competitionTypes, divisions, stages, targetGroups, targetModels, targets, competitions, squads, competitors, users);
	}
}