package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class TargetColour extends Model {

	public String label;

	public TargetColour(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}