package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class CompetitionType extends Model {

	@Column(nullable = false)
	public String label;
	public boolean hasStages;

	public CompetitionType(String label, boolean hasStages) {
		this.label = label;
		this.hasStages = hasStages;
	}

	@Override
	public String toString() {
		return label;
	}
}