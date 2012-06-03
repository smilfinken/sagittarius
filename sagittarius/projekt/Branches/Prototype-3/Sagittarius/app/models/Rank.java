package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Rank extends Model {

	public int ranking;
	public String label;

	public Rank(int ranking) {
		this.ranking = ranking;
		this.label = String.format("%d", ranking);
	}

	public Rank(int ranking, String label) {
		this.ranking = ranking;
		this.label = label;
	}
}