package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Target extends Model {

	public boolean hasPoints;
	public String model;

	public Target() {
		hasPoints = false;
		model = "";
	}
}