package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Rank extends Model {

    public int rank;

    public Rank(int rank) {
        this.rank = rank;
    }
}