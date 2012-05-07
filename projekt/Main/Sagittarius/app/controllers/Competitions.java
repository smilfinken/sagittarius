package controllers;

import java.util.List;
import models.Competition;
import models.CompetitionType;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Competitions extends Controller {

    public static void Display(long competitionID) {
        List<CompetitionType> types = CompetitionType.all().fetch();
        Competition competition = Competition.findById(competitionID);
        render(competition, types);
    }
}