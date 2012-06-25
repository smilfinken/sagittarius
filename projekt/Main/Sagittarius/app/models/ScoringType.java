package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class ScoringType extends Model {

	@Column(nullable = false, unique = true)
	public String label;
	public boolean sumPointsAndTargets;
	public boolean sumPointsOnly;

	public ScoringType(String label) {
		this.label = label;
		this.sumPointsAndTargets = false;
		this.sumPointsOnly = false;
	}

	public ScoringType(String label, boolean sumPointsAndTargets, boolean sumPointsOnly) {
		this.label = label;
		this.sumPointsAndTargets = sumPointsAndTargets;
		this.sumPointsOnly = sumPointsOnly;
	}

	@Override
	public String toString() {
		return label;
	}
}