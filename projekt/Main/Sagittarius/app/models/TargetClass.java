package models;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class TargetClass extends Model {

	public String classification;
	public int maximumRange;
	@OneToOne
	public WeaponCategory weaponCategory;

	public TargetClass(String classification, int maximumRange, WeaponCategory weaponCategory){
		this.classification = classification;
		this.maximumRange = maximumRange;
		this.weaponCategory = weaponCategory;
	}
}