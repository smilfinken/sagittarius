package models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Stage extends Model {

	public String name;
	public int stageIndex;
	@OneToMany(cascade= CascadeType.ALL)
	public List<TargetGroup> targetGroups;

	public Stage(int targetGroups) {
		this.targetGroups = new ArrayList<>(targetGroups);
		this.name = "";
		this.stageIndex = 0;
	}

	public Stage(int targetGroups, String name) {
		this.targetGroups = new ArrayList<>(targetGroups);
		this.name = name;
		this.stageIndex = 0;
	}

	public Stage(int targetGroups, String name, int stageIndex) {
		this.targetGroups = new ArrayList<>(targetGroups);
		this.name = name;
		this.stageIndex = stageIndex;
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