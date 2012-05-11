package controllers;

import java.util.List;
import models.Competition;
import models.CompetitionType;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        List<Competition> competitions = Competition.all().fetch();
	List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
        render(competitions, competitionTypes);
    }
}