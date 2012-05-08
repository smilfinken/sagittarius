package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Target extends Model {

    public Target() {
        hasPoints = false;
    }
    public boolean hasPoints;
}