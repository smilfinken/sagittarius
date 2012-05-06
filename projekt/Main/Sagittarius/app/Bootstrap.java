
import models.Competition;
import models.CompetitionType;
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
        if (CompetitionType.count() == 0) {
            Fixtures.loadModels("competition-types.yml");
        }
        if (Competition.count() == 0) {
            Fixtures.loadModels("dummy-competition.yml");
        }
    }
}