package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class StartingPosition extends Model {

	@Column(nullable = false, unique = true)
	public String label;
	public String description;
	@ManyToOne
	public CompetitionType competitionType;
	public boolean aimingAllowed;
	public boolean weaponHolstered;
	public boolean weaponLoaded;

	public StartingPosition(String label, String description, CompetitionType competitionType) {
		this.label = label;
		this.description = description;
		this.competitionType = competitionType;
		this.aimingAllowed = false;
		this.weaponHolstered = false;
		this.weaponLoaded = false;
	}

	public StartingPosition(String label, String description, CompetitionType competitionType, boolean aimingAllowed, boolean weaponHolstered, boolean weaponLoaded) {
		this.label = label;
		this.description = description;
		this.competitionType = competitionType;
		this.aimingAllowed = aimingAllowed;
		this.weaponHolstered = weaponHolstered;
		this.weaponLoaded = weaponLoaded;
	}

	@Override
	public String toString() {
		return description;
	}
}