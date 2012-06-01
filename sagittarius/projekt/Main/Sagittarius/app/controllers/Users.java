package controllers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import models.*;
import play.i18n.Messages;
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
		render(common.Sorting.sortUsers(users));
	}

	public static void registration(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		render(competition, categories, ranks);
	}

	@Check("admin")
	public static void add(long competitionID, String firstName, String surname, long categoryID, long rankID) {
		Competition competition = Competition.findById(competitionID);
		Category category = Category.findById(categoryID);
		Rank rank = Rank.findById(rankID);
		long userID = -1;

		if (firstName != null && surname != null && category != null && rank != null) {
			User user = new User(firstName, surname, rank, Arrays.asList(category));
			user.save();
			userID = user.id;
		}

		List<User> users = User.all().fetch();
		List<Competitor> competitors = competition.competitors;
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		List<Division> divisions = Division.all().fetch();
		renderTemplate("Competitions/competitors.html", competition, common.Sorting.sortUsers(users), common.Sorting.sortCompetitors(competitors), categories, ranks, divisions, userID);
	}

	@Check("admin")
	public static void edit(long userID, String firstName, String surname, String cardNumber, String email, long categoryID, long rankID, boolean admin, String useraction) {
		User item = User.findById(userID);
		String message = "";

		if (item != null) {
			if (params._contains("useraction")) {
				switch (useraction) {
					case "save":
						item.firstName = firstName;
						item.surname = surname;
						item.cardNumber = cardNumber;
						item.email = email;
						item.rank = (Rank) Rank.findById(rankID);
						item.categories = Arrays.asList((Category) Category.findById(categoryID));
						item.admin = admin;
						item.save();
						list();
						break;
					case "reactivate":
						// TODO: This should send a message to the user with the new password,
						//       or preferably a reactivation link
						item.password = Security.hashPassword("123");
						item.confirmationDate = new Date();
						item.save();
						message = Messages.get("secure.passwordreset");
						break;
					case "delete":
						item.delete();
						list();
						break;
				}
			}
		}

		flash.put("categoryID", item.categories.get(0).id);
		flash.put("rankID", item.rank.id);
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		render(item, categories, ranks, message);
	}

	public static void profile(String firstName, String surname, String cardNumber, String email, long categoryID, long rankID, String useraction) {
		User item = User.find("byEmail", session.get("username")).first();

		flash.put("categoryID", item.categories.get(0).id);
		flash.put("rankID", item.rank.id);
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		render(item, categories, ranks);
	}

	@Check("admin")
	public static void delete(long competitionID, long userID) {
		Competition competition = Competition.findById(competitionID);
		User user = User.findById(userID);

		if (user != null) {
			user.delete();
		}

		List<User> users = User.all().fetch();
		List<Competitor> competitors = competition.competitors;
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		List<Division> divisions = Division.all().fetch();
		renderTemplate("Competitions/competitors.html", competition, common.Sorting.sortUsers(users), common.Sorting.sortCompetitors(competitors), categories, ranks, divisions, userID);
	}
}