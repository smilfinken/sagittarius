package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class CompetitionType extends Model {

	public String label;

	public CompetitionType(String label) {
		this.label = label;
	}

	public boolean hasStages() {
		return true;
	}
}