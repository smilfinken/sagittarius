package controllers;

import java.util.List;
import models.Competition;
import models.CompetitionType;
import models.User;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Application extends Controller {

    @Before
    static void addDefaults() {
        renderArgs.put("connected", Security.connected());
    }	
	
    public static void index() {
        List<Competition> competitions = Competition.all().fetch();
        List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
        render(competitions, competitionTypes);
    }
}