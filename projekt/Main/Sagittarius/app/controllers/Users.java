package controllers;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import play.data.validation.Required;
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
	public static void add(long competitionID, String firstName, String surname, @Required String email, long categoryID, long rankID) {
		Category category = Category.findById(categoryID);
		Rank rank = Rank.findById(rankID);
		long userID = -1;

		if (firstName != null && surname != null && category != null && rank != null) {
			User user = new User(firstName, surname, email, rank, Arrays.asList(category));
			user.save();
			userID = user.id;
		}

		flash.put("userID", userID);
		Competitions.competitors(competitionID);
	}

	@Check("admin")
	public static void edit(long userID, String firstName, String surname, String cardNumber, String email, long organisationID, long categoryID, long rankID, boolean admin, String useraction) {
		User user = User.findById(userID);
		String message = "";

		if (user != null) {
			if (params._contains("useraction")) {
				switch (useraction) {
					case "save":
						user.firstName = firstName;
						user.surname = surname;
						user.cardNumber = cardNumber;
						user.email = email;
						user.organisation = (Organisation) Organisation.findById(organisationID);
						user.rank = (Rank) Rank.findById(rankID);
						user.categories = Arrays.asList((Category) Category.findById(categoryID));
						user.admin = admin;
						user.save();
						list();
						break;
					case "reactivate":
						// TODO: This should send a message to the user with the new password,
						//       or preferably a reactivation link
						user.password = Security.hashPassword("123");
						user.confirmationDate = new Date();
						user.save();
						message = Messages.get("secure.passwordreset");
						break;
					case "delete":
						user.delete();
						list();
						break;
				}
			}
		}

		if (user.organisation != null) {
			flash.put("organisationID", user.organisation.id);
		}
		flash.put("categoryID", user.categories.get(0).id);
		flash.put("rankID", user.rank.id);
		List<Organisation> organisations = Organisation.all().fetch();
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		render(user, organisations, categories, ranks, message);
	}

	public static void profile(long userID, String firstName, String surname, String cardNumber, String email, long categoryID, long rankID, String useraction) {
		User user = User.find("byEmail", session.get("username")).first();

		if (user != null) {
			if (params._contains("useraction")) {
				switch (useraction) {
					case "save":
						user.firstName = firstName;
						user.surname = surname;
						user.cardNumber = cardNumber;
						user.email = email;
						user.rank = (Rank) Rank.findById(rankID);
						user.categories = Arrays.asList((Category) Category.findById(categoryID));
						user.save();

						Application.index();
						break;
				}
			}
		}

		flash.put("categoryID", user.categories.get(0).id);
		flash.put("rankID", user.rank.id);
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		render(user, categories, ranks);
	}

	public static void editPassword(long userID, String oldPassword, String verifyPassword, String newPassword, String useraction) {
		User user = User.find("byEmail", session.get("username")).first();
		String message = "";

		if (user != null) {
			if (params._contains("useraction")) {
				switch (useraction) {
					case "save":
						if (oldPassword.equals(verifyPassword) && Security.validatePassword(oldPassword, user.password)) {
							user.password = Security.hashPassword(newPassword);
							user.confirmationDate = new Date();
							user.save();
							message = Messages.get("secure.passwordreset");
							flash.put("categoryID", user.categories.get(0).id);
							flash.put("rankID", user.rank.id);
							List<Organisation> organisations = Organisation.all().fetch();
							List<Category> categories = Category.all().fetch();
							List<Rank> ranks = Rank.all().fetch();

							Application.index();
						} else {
							message = Messages.get("secure.passwordnotverified");
							render(user);
						}
						break;
				}
			}
		}

		render(user);
	}

	private static Document parseXML(File file) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		return document;
	}

	public static void importUsers(File file, String useraction) {
		if (params._contains("useraction")) {
			switch (useraction) {
				case "upload":
					if (file != null) {
						try {
							for (Iterator it = parseXML(file).selectNodes("//User").iterator(); it.hasNext();) {
								Node userNode = (Node) it.next();
								User user = new User(userNode);
							}
							list();
						} catch (DocumentException ex) {
							Logger.getLogger(Competitions.class.getName()).log(Level.SEVERE, "Failed to import XML from file", ex);
						}
						break;
					}
			}
		}

		list();
	}
}
