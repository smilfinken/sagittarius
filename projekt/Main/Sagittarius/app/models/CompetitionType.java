package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class CompetitionType extends Model {

	public String name;

	public CompetitionType(String name) {
		this.name = name;
	}

	public boolean hasStages() {
		return true;
	}
}