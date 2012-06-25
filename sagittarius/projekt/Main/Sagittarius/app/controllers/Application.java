package controllers;

import java.util.ArrayList;
import java.util.List;
import models.Competition;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Application extends Controller {

	public static void index() {
		User user = User.find("byEmail", session.get("username")).first();
		List<Competition> competitions = new ArrayList<>();
		if (user != null && user.admin) {
			competitions = Competition.all().fetch();
		}

		List<Competition> entries = competitions;
		render(competitions, entries);
	}
}