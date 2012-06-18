
import models.*;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

/**
 *
 * @author johan
 */
@OnApplicationStart
public class Bootstrap extends Job {

	@Override
	public void doJob() {
		// always check before loading fixtures now that we're sometimes using a pre-loaded db

		// load semi-static parameters
		if (CompetitionType.count() == 0) {
			Fixtures.loadModels("Defaults/competition-types.yml");
		}
		if (ScoringType.count() == 0) {
			Fixtures.loadModels("Defaults/scoring-types.yml");
		}
		if (StartingPosition.count() == 0) {
			Fixtures.loadModels("Defaults/starting-positions.yml");
		}
		if (Category.count() == 0) {
			Fixtures.loadModels("Defaults/categories.yml");
		}
		if (Division.count() == 0) {
			Fixtures.loadModels("Defaults/divisions.yml");
		}
		if (Rank.count() == 0) {
			Fixtures.loadModels("Defaults/ranks.yml");
		}
		if (WeaponCategory.count() == 0) {
			Fixtures.loadModels("Defaults/weapon-categories.yml");
		}
		if (TargetColour.count() == 0) {
			Fixtures.loadModels("Defaults/target-colours.yml");
		}
		if (TargetClass.count() == 0) {
			Fixtures.loadModels("Defaults/target-classes.yml");
		}
		if (TargetShape.count() == 0) {
			Fixtures.loadModels("Defaults/target-shapes.yml");
		}

		// load dummy data
		if (User.count() == 0) {
			Fixtures.loadModels("Testdata/dummy-users.yml");
		}
		if (Competition.count() == 0) {
			Fixtures.loadModels("Testdata/dummy-competitions.yml");
		}
	}
}
