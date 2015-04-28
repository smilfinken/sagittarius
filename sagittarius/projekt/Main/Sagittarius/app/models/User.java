package models;

import controllers.Security;
import java.util.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import notifiers.RegistrationNotifier;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.yecht.Data;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class User extends Model {

	public String firstName;
	public String surname;
	@Column(unique = true, nullable = false)
	public String email;
	public String cardNumber;
	@OneToOne
	public Organisation organisation;
	public String password;
	public Date registrationDate;
	public Date confirmationDate;
	public boolean admin = false;
	@ManyToOne
	public Rank rank;
	@ManyToMany
	public List<Category> categories;
	public String phone;
	public String address;
	public String postalCode;
	public String city;
	public Date memberSince;
	public Date birthDate;

	public User(String firstName, String surname, String email) {
		this.firstName = firstName;
		this.surname = surname;
		this.email = email;
	}

	public User(String firstName, String surname, String cardNumber, String email, Rank rank, List<Category> categories, String password) {
		this.firstName = firstName;
		this.surname = surname;
		this.email = email;
		this.cardNumber = cardNumber;
		this.rank = rank;
		this.categories = categories;
		this.password = Security.hashPassword(password);
	}

	public User(String firstName, String surname, String email, Rank rank, List<Category> categories) {
		this.firstName = firstName;
		this.surname = surname;
		this.email = email;
		this.rank = rank;
		this.categories = categories;
	}

	public User(Node user) {
		this.firstName = user.valueOf("@firstname");
		this.surname = user.valueOf("@surname");
		this.cardNumber = user.valueOf("@cardnumber");
		this.email = user.valueOf("@email");
		if (this.email == null || this.email.length() == 0) {
			this.email = String.format("%s.%s@%s", firstName, surname, surname);
		}
		this.rank = Rank.find("byLabel", user.valueOf("@rank")).first();
		this.categories = new ArrayList<>();
		for (Iterator it = user.selectNodes("Category").iterator(); it.hasNext();) {
			Node categoryNode = (Node) it.next();
			Category category = Category.find("byLabel", categoryNode.valueOf("@label")).first();
			this.categories.add(category);
		}
		this.save();
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
	public boolean create() {
		this.registrationDate = new Date();
		return super.create();
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
		userElement.addAttribute("rank", rank.toString());

		for (Category category : categories) {
			userElement.add(category.toXML());
		}
		return userElement;
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

	public Set<Division> getValidDivisions() {
		Set<Division> divisions = new TreeSet<>(new Comparator<Division>() {

			@Override
			public int compare(Division o1, Division o2) {
				if (o1 != null && o2 != null) {
					return o1.id.compareTo(o2.id);
				}
				return 0;
			}
		});
		for (Category c : categories) {
			divisions.addAll(c.divisions);
		}
		return divisions;
	}
}
