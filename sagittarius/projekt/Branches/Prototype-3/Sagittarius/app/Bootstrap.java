
import controllers.Security;
import java.util.Date;
import java.util.List;
import models.*;
import org.xhtmlrenderer.extend.OutputDevice;
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
		} else {
			// try to fix missing labels on existing ranks
			List<Rank> ranks = Rank.all().fetch();
			for (Rank rank : ranks) {
				if (rank.label == null || rank.label == "") {
					rank.label = String.format("%d", rank.ranking);
					rank.save();
				}
			}
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
			for (User user : users) {
				if (user.password == null || "123".equals(user.password)) {
					user.password = Security.hashPassword("123");
					user.registrationDate = new Date();
					user.confirmationDate = new Date();
					user.save();
				}
			}
		}
		if (Competition.count() == 0) {
			Fixtures.loadModels("Testdata/dummy-competitions.yml");
		} else {
			// try to fix missing indexes on stages
			List<Competition> competitions = Competition.all().fetch();
			for (Competition competition : competitions) {
				int index = 1;
				for (Stage stage : competition.stages) {
					if (stage.stageIndex == 0) {
						stage.stageIndex = index++;
						stage.save();
					}
				}
			}
		}
	}
}
