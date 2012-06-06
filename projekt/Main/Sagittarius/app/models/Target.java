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

	@ManyToOne
	public TargetShape targetShape;
	@ManyToOne
	public TargetColour targetColour;
	public boolean hasPoints;

	public Target(TargetShape targetShape, TargetColour targetColour) {
		this.targetShape = targetShape;
		this.targetColour = targetColour;
		this.hasPoints = false;
	}

	public Target(TargetShape targetShape, TargetColour targetColour, boolean hasPoints) {
		this.targetShape = targetShape;
		this.targetColour = targetColour;
		this.hasPoints = hasPoints;
	}

	@Override
	public String toString() {
		if (targetShape != null && targetColour != null) {
			return String.format("%s %s", targetColour.toString(), targetShape.toString());
		} else {
			return "";
		}
	}

	public long getShapeID() {
		if (targetShape != null) {
			return targetShape.id;
		}

		return -1;
	}

	public long getColourID() {
		if (targetColour != null) {
			return targetColour.id;
		}

		return -1;
	}
}