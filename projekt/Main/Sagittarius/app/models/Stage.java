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
	public List<TargetGroup> targetGroups;

	public Stage(int targetGroups) {
		this.targetGroups = new ArrayList<>(targetGroups);
		this.name = "";
	}

	public Stage(int targetGroups, String name) {
		this.targetGroups = new ArrayList<>(targetGroups);
		this.name = name;
	}

	public int targetCount() {
		int targets = 0;
		if (targetGroups != null) {
			for (TargetGroup group : targetGroups) {
				if (group.targets != null) {
					targets += group.targets.size();
				}
			}
		}
		return targets;
	}

	public boolean hasPoints() {
		if (targetGroups != null) {
			for (TargetGroup group : targetGroups) {
				if (group.targets != null) {
					for (Target target : group.targets) {
						if (target.hasPoints) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}