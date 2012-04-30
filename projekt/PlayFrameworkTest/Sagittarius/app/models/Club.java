/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import javax.persistence.Entity;
import javax.persistence.Table;
import play.db.jpa.Model;

/**
 *
 * @author ancla
 */
@Entity
@Table(name="tblClub")
public class Club extends Model {
    public String name;
    public String street;
    public String zipCode;
    public String city;

    public Club(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Club{" + "name=" + name + ", street=" + street + ", zipCode=" + zipCode + ", city=" + city + '}';
    }
    
}
