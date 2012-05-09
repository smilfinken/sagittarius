package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Category extends Model {

    public Category(String name) {
        this.name = name;
    }
    public String name;
}