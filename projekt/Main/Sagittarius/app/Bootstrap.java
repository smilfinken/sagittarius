
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
        // clean up default data
        Fixtures.deleteDatabase();
        Fixtures.loadModels("Defaults/competition-types.yml");
        Fixtures.loadModels("Defaults/categories.yml");
        Fixtures.loadModels("Defaults/ranks.yml");

        // load dummy data
        Fixtures.loadModels("Testdata/dummy-users.yml");
        Fixtures.loadModels("Testdata/dummy-competitions.yml");
    }
}