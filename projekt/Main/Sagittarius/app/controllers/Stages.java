package controllers;

import java.util.List;
import models.Competition;
import models.Stage;
import play.db.jpa.GenericModel;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Stages extends Controller {

    public static void list(int competitionID) {
        Competition competition = Competition.findById(competitionID);
        List<Stage> stages = competition.stages;
        render(stages);
    }
}