package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import play.db.jpa.Model;
import play.i18n.Messages;

/**
 *
 * @author johan
 */
@Entity
public class TargetClass extends Model {

	@Column(nullable = false)
	public String classification;
	public int maximumRange;
	@OneToOne
	public WeaponCategory weaponCategory;

	public TargetClass(String classification, int maximumRange, WeaponCategory weaponCategory) {
		this.classification = classification;
		this.maximumRange = maximumRange;
		this.weaponCategory = weaponCategory;
	}

	@Override
	public String toString() {
		return String.format("%s %s, %s %s", Messages.get("targetclass.label"), classification, Messages.get("weaponcategory.label"), weaponCategory.shortLabel);
	}
}