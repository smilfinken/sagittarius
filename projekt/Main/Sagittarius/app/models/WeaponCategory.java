package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class WeaponCategory extends Model{
	@Column(nullable=false)
	public String label;
	@Column(nullable=false)
	public String shortLabel;
	public double shotInterval;

	public WeaponCategory(String label, String shortLabel)
	{
		this.label = label;
		this.shortLabel = shortLabel;
		this.shotInterval = 0;
	}

	public WeaponCategory(String label, String shortLabel, double shotInterval)
	{
		this.label = label;
		this.shortLabel = shortLabel;
		this.shotInterval = shotInterval;
	}

	@Override
	public String toString(){
		return label;
	}
}