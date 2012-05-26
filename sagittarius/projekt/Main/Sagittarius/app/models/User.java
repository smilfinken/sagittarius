package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import notifiers.RegistrationNotifier;
import play.db.jpa.Model;
import controllers.Security;

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
	
	@Override
	public boolean create() {
		this.registrationDate = new Date();
		return super.create();
	}

	public String getFullName() {
		return String.format("%s %s", firstName, surname);
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
}