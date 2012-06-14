package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import notifiers.RegistrationNotifier;
import play.db.jpa.Model;
import controllers.Security;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 *
 * @author johan
 */
@Entity
public class User extends Model {

	public String firstName;
	public String surname;
	public String cardNumber;
	public String email;
	public String password;
	public Date registrationDate;
	public Date confirmationDate;
	public boolean admin = false;
	@OneToOne
	public Rank rank;
	@ManyToMany
	public List<Category> categories;

	public User(String firstName, String surname) {
		this.firstName = firstName;
		this.surname = surname;
	}

	public User(String firstName, String surname, String cardNumber, Rank rank, List<Category> categories, String email, String password) {
		this.firstName = firstName;
		this.surname = surname;
		this.cardNumber = cardNumber;
		this.rank = rank;
		this.categories = categories;
		this.email = email;
		this.password = Security.hashPassword(password);
	}

	public User(String firstName, String surname, Rank rank, List<Category> categories) {
		this.firstName = firstName;
		this.surname = surname;
		this.rank = rank;
		this.categories = categories;
	}

	public static boolean connect(String username, String password) {
		boolean result = false;
		User user = User.find("byEmail", username).first();

		if (user != null && user.confirmationDate != null) {
			result = Security.validatePassword(password, user.password);
		}

		return result;
	}

	@Override
	public String toString() {
		return email;
	}

	public Element toXML() {
		Element userElement = DocumentHelper.createElement(this.getClass().getSimpleName());
		userElement.addAttribute("firstname", firstName);
		userElement.addAttribute("surname", surname);
		userElement.addAttribute("email", email);
		userElement.addAttribute("cardnumber", cardNumber);
		userElement.addAttribute("rank", rank.label);

		for (Category category : categories) {
			userElement.add(category.toXML());
		}
		return userElement;
	}

	@Override
	public boolean create() {
		this.registrationDate = new Date();
		return super.create();
	}

	public String getFullName() {
		return this.getFullName(false);
	}

	public String getFullName(boolean reverse) {
		if (reverse) {
			return String.format("%s, %s", surname, firstName);
		} else {
			return String.format("%s %s", firstName, surname);
		}
	}

	public void sendRegistration() {
		RegistrationNotifier.welcome(this, generateRegistrationHash());
	}

	private String generateRegistrationHash() {
		return Security.staticHash(this.registrationDate.toString());
	}

	public boolean confirmRegistration(String hash) {
		boolean ok = hash.equals(Security.staticHash(this.registrationDate.toString()));
		if (ok) {
			this.confirmationDate = new Date();
			save();
		}
		return ok;
	}

	public static boolean check(String username, String profile) {
		if (username != null && profile != null && !"".equals(profile)) {
			switch (profile) {
				case "basic":
					return true;
				case "admin":
					User user = User.find("byEmail", username).first();
					if (user != null) {
						return user.admin;
					}
				default:
					return false;
			}
		}
		return false;
	}
}