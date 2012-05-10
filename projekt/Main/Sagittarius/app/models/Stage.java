package models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Stage extends Model {

	public String name;
	@ManyToMany
	public List<Target> targets;

	public Stage(int targets) {
		this.targets = new ArrayList<>(targets);
		this.name = "";
	}

	public Stage(int targets, String name) {
		this.targets = new ArrayList<>(targets);
		this.name = name;
	}

	public int targetCount() {
		if (targets != null) {
			return targets.size();
		} else {
			return 0;
		}
	}

	public boolean hasPoints() {
		for (Target target : targets) {
			if (target.hasPoints) {
				return true;
			}
		}
		return false;
	}
}