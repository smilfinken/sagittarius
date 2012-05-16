	package controllers;

import java.util.List;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Users extends Controller {

	public static void list() {
		List<User> users = User.all().fetch();

		render(users);
	}
}