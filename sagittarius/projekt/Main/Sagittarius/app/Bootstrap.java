
import models.*;
import play.jobs.Job;
import play.test.Fixtures;

/**
 *
 * @author johan
 */
//@OnApplicationStart
public class Bootstrap extends Job {

    @Override
    public void doJob() {
        // load default data
        if (CompetitionType.count() == 0) {
            Fixtures.loadModels("Defaults/competition-types.yml");
        }
        if (Category.count() == 0) {
            Fixtures.loadModels("Defaults/categories.yml");
        }
        if (Rank.count() == 0) {
            Fixtures.loadModels("Defaults/ranks.yml");
        }

        // load dummy data
        if (Competition.count() == 0) {
            Fixtures.loadModels("Testdata/dummy-competition.yml");
        }
        if (User.count() == 0) {
            Fixtures.loadModels("Testdata/dummy-users.yml");
        }
    }
}