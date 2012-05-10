package models;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class User extends Model {

	public String firstName;
	public String surname;
	@OneToOne
	public Rank rank;
	@OneToOne
	public Category category;

	public User(String firstName, String surname) {
		this.firstName = firstName;
		this.surname = surname;
	}
}