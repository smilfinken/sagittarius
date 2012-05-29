
import controllers.Security;
import java.util.List;
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
		if (Category.count() == 0) {
			Fixtures.loadModels("Defaults/categories.yml");
		}
		if (Division.count() == 0) {
			Fixtures.loadModels("Defaults/divisions.yml");
		}
		if (Rank.count() == 0) {
			Fixtures.loadModels("Defaults/ranks.yml");
		}
		if (TargetModel.count() == 0) {
			Fixtures.loadModels("Defaults/target-models.yml");
		}

		// load dummy data
		if (User.count() == 0) {
			Fixtures.loadModels("Testdata/dummy-users.yml");
		} else {
			// try to compensate for old data not having hashed passwords
			List<User> users = User.all().fetch();
			for (User user : users){
				if ("123".equals(user.password)){
					user.password = Security.hashPassword("123");
				}
			}
		}
		if (Competition.count() == 0) {
			Fixtures.loadModels("Testdata/dummy-competitions.yml");
		}
	}
}