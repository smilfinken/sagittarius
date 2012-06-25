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
						item.save();

						Application.index();
						break;
				}
			}
		}

		flash.put("categoryID", item.categories.get(0).id);
		flash.put("rankID", item.rank.id);
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		render(item, categories, ranks);
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