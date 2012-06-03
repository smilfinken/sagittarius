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

	@Check("admin")
	public static void edit(long competitionTypeID, String name, String useraction) {
		CompetitionType item = CompetitionType.findById(competitionTypeID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					item.name = name;
					item.save();
					list();
					break;
				case "delete":
					item.delete();
					list();
					break;
			}

		}

		render(item);
	}
}