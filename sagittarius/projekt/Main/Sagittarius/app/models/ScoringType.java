package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class ScoringType extends Model {

	public String label;
	public boolean sumPointsAndTargets;

	public ScoringType(String label) {
		this.label = label;
		this.sumPointsAndTargets = false;
	}

	public ScoringType(String label, boolean sumPointsAndTargets) {
		this.label = label;
		this.sumPointsAndTargets = sumPointsAndTargets;
	}

	@Override
	public String toString() {
		return label;
	}
}