package controllers;

import java.util.List;
import models.Division;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Divisions extends Controller {

	public static void list() {
		List<Division> divisions = Division.all().fetch();
		render(divisions);
	}

	public static void edit(long divisionID) {
	}
}