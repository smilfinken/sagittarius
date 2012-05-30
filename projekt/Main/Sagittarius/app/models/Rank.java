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
	public String label;

	public Rank(int rank) {
		this.rank = rank;
		this.label = String.format("%d", rank);
	}

	public Rank(int rank, String label) {
		this.rank = rank;
		this.label = label;
	}
}