package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Category extends Model {

    public String name;

    public void Category(String name){
        this.name = name;
    }
}