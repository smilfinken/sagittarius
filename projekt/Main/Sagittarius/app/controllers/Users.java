	package controllers;

import java.util.List;

import models.VerifiedUser;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Users extends Controller {

	public static void list() {
		List<VerifiedUser> users = VerifiedUser.all().fetch();

		render(users);
	}
}