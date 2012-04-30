/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import javax.persistence.*;
import play.db.jpa.Model;

/**
 *
 * @author ancla
 */
@Entity
@Table(name = "tblUser")
@NamedQueries({
    @NamedQuery(name = "getAllUsers", query = "SELECT u FROM User u WHERE u.deleted != true"),
    @NamedQuery(name = "getUserByUsername", query = "SELECT u FROM User u WHERE u.deleted != true AND u.userName = :username"),
})
public class User extends Model {

    @Column(unique = true, nullable = false)
    public String userName;
    public String firstName;
    public String lastName;
    public String eMail;
    public boolean deleted = false;
    @Override
    public String toString() {
        return "User{" + "id=" + id + ", userName=" + userName + ", firstName=" + firstName + ", lastName=" + lastName + ", eMail=" + eMail + ", deleted=" + deleted + '}';
    }

    public User(String userName, String firstName, String lastName, String eMail) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.eMail = eMail;
    }
    
    @Override
    public User delete() {
        this.deleted = true;
        save();
        return this;
    } 
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if ((this.userName == null) ? (other.userName != null) : !this.userName.equals(other.userName)) {
            return false;
        }
        if ((this.firstName == null) ? (other.firstName != null) : !this.firstName.equals(other.firstName)) {
            return false;
        }
        if ((this.lastName == null) ? (other.lastName != null) : !this.lastName.equals(other.lastName)) {
            return false;
        }
        if ((this.eMail == null) ? (other.eMail != null) : !this.eMail.equals(other.eMail)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.userName != null ? this.userName.hashCode() : 0);
        hash = 83 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 83 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        hash = 83 * hash + (this.eMail != null ? this.eMail.hashCode() : 0);
        return hash;
    }
        
}
