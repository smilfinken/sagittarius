package controllers;

import java.util.Arrays;
import java.util.List;
import models.*;
import play.db.jpa.GenericModel;
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

	public static void registration(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();

		render(competition, categories, ranks);
	}

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
		List<Competitor> competitors = Competitor.all().fetch();
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		List<Division> divisions = Division.all().fetch();
		renderTemplate("Competitions/competitors.html", competition, common.Sorting.sortUsers(users), common.Sorting.sortCompetitors(competitors), categories, ranks, divisions, userID);
	}

	public static void delete(long competitionID, long userID){
		Competition competition = Competition.findById(competitionID);
		User user = User.findById(userID);

		if (user != null){
			user.delete();
		}

		List<User> users = User.all().fetch();
		List<Competitor> competitors = Competitor.all().fetch();
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		List<Division> divisions = Division.all().fetch();
		renderTemplate("Competitions/competitors.html", competition, common.Sorting.sortUsers(users), common.Sorting.sortCompetitors(competitors), categories, ranks, divisions, userID);
	}
}