package controllers;

import java.util.Arrays;
import java.util.List;
import models.*;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Users extends Controller {

	public static void list() {
		List<User> users = User.all().fetch();

		render(users);
	}
}