package models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Stage extends Model {

	public String label;
	public int stageIndex;
	@OneToMany(cascade = CascadeType.ALL)
	public List<TargetGroup> targetGroups;
	@ManyToOne
	public StartingPosition startingPosition;

	private List<TargetGroup> createTargetGroups(int count) {
		ArrayList newGroups = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			TargetGroup targetGroup = new TargetGroup();
			targetGroup.save();
			newGroups.add(targetGroup);
		}
		return newGroups;
	}

	public Stage(int targetGroups) {
		this.targetGroups = createTargetGroups(targetGroups);
		this.label = "";
		this.stageIndex = 0;
	}

	public Stage(int targetGroups, String label) {
		this.targetGroups = createTargetGroups(targetGroups);
		this.label = label;
		this.stageIndex = 0;
	}

	public Stage(int targetGroups, String label, int stageIndex) {
		this.targetGroups = createTargetGroups(targetGroups);
		this.label = label;
		this.stageIndex = stageIndex;
	}

	public Stage(Node stage) {
		this.label = stage.valueOf("@label");
		this.stageIndex = new Integer(stage.valueOf("@stageindex"));
		this.startingPosition = StartingPosition.find("byLabel", stage.valueOf("@startingposition")).first();
		this.save();
	}

	@Override
	public String toString() {
		return label;
	}

	public Element toXML() {
		Element stageElement = DocumentHelper.createElement(this.getClass().getSimpleName());
		stageElement.addAttribute("label", label);
		stageElement.addAttribute("stageindex", String.format("%d", stageIndex));
		stageElement.addAttribute("startingposition", startingPosition.label);
		for (TargetGroup targetGroup : targetGroups) {
			stageElement.add(targetGroup.toXML());
		}
		return stageElement;
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

	public void deleteTargetGroup(long targetGroupID) {
		TargetGroup targetGroup = TargetGroup.findById(targetGroupID);
		if (targetGroup != null && this.targetGroups.contains(targetGroup)) {
			targetGroup.deleteTargets();
			this.targetGroups.remove(targetGroup);
			this.save();
			targetGroup.delete();
		}
	}

	public void deleteTargetGroups() {
		ArrayList<Long> ids = new ArrayList<>();
		for (TargetGroup item : this.targetGroups) {
			ids.add(item.id);
		}
		for (long item : ids) {
			deleteTargetGroup(item);
		}
		this.targetGroups = null;
		this.save();
	}
}