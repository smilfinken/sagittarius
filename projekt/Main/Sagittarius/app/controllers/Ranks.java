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
	public static void edit(long rankID) {
	}
}