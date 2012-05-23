package models;

import controllers.Security;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import notifiers.RegistrationNotifier;
import play.db.jpa.Model;

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
	public boolean pending;
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

		if (user != null) {
			result = Security.validatePassword(password, user.password);
		}

		return result;
	}

	@Override
	public String toString() {
		return email;
	}

	public String getFullName() {
		return String.format("%s %s", firstName, surname);
	}

	public void sendRegistration() {
		RegistrationNotifier.welcome(this);
	}
}