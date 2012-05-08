package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Category extends Model {

    public Category(String name, String shortName) {
        this.shortName = shortName;
        this.name = name;
    }
    public String shortName;
    public String name;
}