package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Organisation extends Model {

	@Column(nullable = false, unique = true)
	public String label;
	@Column(nullable = false, unique = true)
	public String shortname;

	public Organisation(String label) {
		this.label = label;
	}

	public Organisation(String label, String shortname) {
		this.label = label;
		this.shortname = shortname;
	}

	@Override
	public String toString() {
		return this.label;
	}
}