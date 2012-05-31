package controllers;

import java.util.List;
import models.CompetitionType;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class CompetitionTypes extends Controller {

	public static void list() {
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		render(competitionTypes);
	}

	public static void edit(long competitionTypeID) {
	}
}