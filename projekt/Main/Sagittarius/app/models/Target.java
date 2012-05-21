package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Target extends Model {

	public boolean hasPoints;
	@ManyToOne
	public TargetModel targetModel;

	public Target() {
		hasPoints = false;
		targetModel = new TargetModel("Okänd typ", "Valfri färg");
	}

	public String getModel() {
		if (targetModel != null) {
			return targetModel.getModel();
		}

		return "";
	}

	public long getModelID() {
		if (targetModel != null) {
			return targetModel.id;
		}

		return -1;
	}
}