package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Result extends Model {

	public int hits;
	public int targets;
	public int points;

	public Result(int hits, int targets, int points) {
		this.hits = hits;
		this.targets = targets;
		this.points = points;
	}
}