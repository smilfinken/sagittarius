package controllers;

import java.util.List;
import models.Rank;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Ranks extends Controller {

	public static void list() {
		List<Rank> ranks = Rank.all().fetch();
		render(ranks);
	}

	@Check("admin")
	public static void edit(long rankID, int rank, String label, String useraction) {
		Rank item = Rank.findById(rankID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					item.rank = rank;
					item.label = label;
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