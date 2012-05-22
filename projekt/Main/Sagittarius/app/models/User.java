package models;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import notifiers.RegistrationNotifier;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import play.db.jpa.Model;
import play.i18n.Messages;
import play.libs.Mail;

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

	public User(String firstName, String surname, String cardNumber,
		String email, String password) {
		this.firstName = firstName;
		this.surname = surname;
		this.cardNumber = cardNumber;
		this.email = email;
		this.password = password;
	}

	public User(String firstName, String surname, Rank rank, List<Category> categories) {
		this.firstName = firstName;
		this.surname = surname;
		this.rank = rank;
		this.categories = categories;
	}

	public static User connect(String username, String password) {
		return find("byEmailAndPassword", username, password).first();
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