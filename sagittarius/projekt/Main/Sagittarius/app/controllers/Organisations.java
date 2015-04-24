package controllers;

import java.util.List;
import models.Organisation;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Organisations extends Controller {
	public static void list() {
		List<Organisation> organisations = Organisation.all().fetch();
		render(organisations);
	}
}