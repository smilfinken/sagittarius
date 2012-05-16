package models;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.InheritanceType;
import javax.persistence.DiscriminatorType;
import javax.persistence.Inheritance;

import play.db.jpa.Model;
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING, name="status")
public abstract class UserAttr extends Model {

	public String firstName;
	public String surname;
	public String cardNumber;
	public String email;
	public String password;

}