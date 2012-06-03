package controllers;

import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Administration extends Controller {

	@Check("admin")
	public static void index() {
		render();
	}
}
