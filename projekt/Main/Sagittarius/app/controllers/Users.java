package controllers;

import java.util.Arrays;
import java.util.List;
import models.*;
import play.data.validation.Required;
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
		User user = User.findById(userID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					user.firstName = firstName;
					user.surname = surname;
					user.cardNumber = cardNumber;
					user.email = email;
					user.rank = (Rank) Rank.findById(rankID);
					user.categories = Arrays.asList((Category) Category.findById(categoryID));
					user.admin = admin;
					user.save();
					break;
				case "delete":
					user.delete();
					list();
					break;
			}

		}

		flash.put("categoryID", user.categories.get(0).id);
		flash.put("rankID", user.rank.id);
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		render(user, categories, ranks);
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