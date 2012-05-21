package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class ScoringType extends Model {

	public int type;
	public String label;

	public ScoringType(int type, String label) {
		this.type = type;
		this.label = label;
	}
}