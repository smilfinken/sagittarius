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
@Table(name="tblCompetitor")
public class Competitor extends Model {
    
    public String firstName;
    public String lastName;
    public String cardNumber;
    public String street;
    public String zipCode;
    public String city;
    
    @ManyToOne(optional=false)
    public Club club;
    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    public CompetingClass competingClass;
    
    public Competitor(String firstName, String lastName, Club club, CompetingClass competingClass) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = club;
        this.competingClass = competingClass;
    }
    
    public enum CompetingClass {

        ONE ( "1" ),
        TWO ( "2" ), 
        THREE ( "3" );

        private String competingClass;

        private CompetingClass(String competingClass) {
            this.competingClass = competingClass;
        }

        @Override
        public String toString() {
            return competingClass;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Competitor other = (Competitor) obj;
        if ((this.firstName == null) ? (other.firstName != null) : !this.firstName.equals(other.firstName)) {
            return false;
        }
        if ((this.lastName == null) ? (other.lastName != null) : !this.lastName.equals(other.lastName)) {
            return false;
        }
        if ((this.cardNumber == null) ? (other.cardNumber != null) : !this.cardNumber.equals(other.cardNumber)) {
            return false;
        }
        if ((this.street == null) ? (other.street != null) : !this.street.equals(other.street)) {
            return false;
        }
        if ((this.zipCode == null) ? (other.zipCode != null) : !this.zipCode.equals(other.zipCode)) {
            return false;
        }
        if ((this.city == null) ? (other.city != null) : !this.city.equals(other.city)) {
            return false;
        }
        if (this.competingClass != other.competingClass) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Competitor{" + "firstName=" + firstName + ", lastName=" + lastName + ", cardNumber=" + cardNumber + ", street=" + street + ", zipCode=" + zipCode + ", city=" + city + ", club=" + club + ", competingClass=" + competingClass + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 41 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        hash = 41 * hash + (this.zipCode != null ? this.zipCode.hashCode() : 0);
        hash = 41 * hash + (this.city != null ? this.city.hashCode() : 0);
        hash = 41 * hash + (this.competingClass != null ? this.competingClass.hashCode() : 0);
        return hash;
    }
    
}
