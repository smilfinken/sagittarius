package models;

import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
@DiscriminatorValue("VERIFIED") 
public class User extends UserAttr {

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
	
	public User(String firstName, String surname, String cardNumber,
			String email, String password, Rank rank, List<Category> categories) {
		this.firstName = firstName;
		this.surname = surname;
		this.cardNumber = cardNumber;
		this.email = email;
		this.password = password;
		this.rank = rank;
		this.categories = categories;		
	}

	public User(String firstName, String surname, Rank rank, List<Category> categories){
		this.firstName = firstName;
		this.surname = surname;
		this.rank = rank;
		this.categories = categories;
	}
	public static UserAttr connect(String username, String password) {
		return find("byEmailAndPassword", username, password).first();
	}
	public String toString(){
		return email;
	}
}