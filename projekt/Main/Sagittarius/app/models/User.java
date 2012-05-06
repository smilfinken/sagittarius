package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class User extends Model {

    public String firstName;
    public String surname;
    public Rank rank;
    public Category category;

    public void User(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
    }
}