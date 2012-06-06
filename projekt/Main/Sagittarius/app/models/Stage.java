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

	public String label;
	public int stageIndex;
	@OneToMany(cascade= CascadeType.ALL)
	public List<TargetGroup> targetGroups;

	public Stage(int targetGroups) {
		this.targetGroups = new ArrayList<>(targetGroups);
		this.label = "";
		this.stageIndex = 0;
	}

	public Stage(int targetGroups, String label) {
		this.targetGroups = new ArrayList<>(targetGroups);
		this.label = label;
		this.stageIndex = 0;
	}

	public Stage(int targetGroups, String label, int stageIndex) {
		this.targetGroups = new ArrayList<>(targetGroups);
		this.label = label;
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