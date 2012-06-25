package controllers;

import java.util.Arrays;
import java.util.List;
import models.Category;
import models.Rank;
import models.User;
import play.data.validation.Email;
import play.data.validation.Min;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Registration extends Controller {

	public static void signup() {
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();

		render(categories, ranks);
	}

	public static void doSignup(@Required String firstname, @Required String surname, String cardnumber, @Min(0) long categoryID, @Min(0) long rankID, @Required @Email String email, @Required String password, @Required String passwordVerification) {
		Validation.equals("passwordVerification", password, Messages.get("controller.registration.password"), passwordVerification);
		if (Validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			Validation.keep(); // keep the errors for the next request
			signup();
		}

		Rank rank = Rank.findById(rankID);
		Category category = Category.findById(categoryID);
		User user = new User(firstname, surname, cardnumber, email, rank, Arrays.asList(category), password);
		if (user.create()) {
			user.sendRegistration();
			render();
		}
	}

	public static void confirm(@Required String hash, @Required String email) {
		User user = User.find("byEmail", email).first();
		if (user != null && user.confirmRegistration(hash)) {
			confirmed();
		} else {
			failed();
		}
	}

	public static void failed() {
		render();
	}

	public static void confirmed() {
		render();
	}
}