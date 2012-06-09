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
	public boolean hasStages;

	public CompetitionType(String label, boolean hasStages) {
		this.label = label;
		this.hasStages = hasStages;
	}
}