package controllers;

import java.util.List;
import models.Competition;
import models.Stage;
import play.db.jpa.GenericModel;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Stages extends Controller {

    public static void list(int competitionID) {
        Competition competition = Competition.findById(competitionID);
        List<Stage> stages = competition.stages;
        render(stages);
    }
}