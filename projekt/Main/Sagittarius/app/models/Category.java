package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Category extends Model {

    public String label;

    public void Category(String label){
        this.label = label;
    }
}