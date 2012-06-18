package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
public class TargetGroup extends Model {

	public String label;
	public int range;
	@OneToMany(cascade = CascadeType.ALL)
	public List<Target> targets;

	public TargetGroup() {
		this.label = "";
		this.range = 0;
		this.targets = new ArrayList<>();
	}

	public TargetGroup(List<Target> targets) {
		this.label = "";
		this.targets = targets;
	}

	public TargetGroup(String label, int range, List<Target> targets) {
		this.label = label;
		this.range = range;
		this.targets = targets;
	}

	public TargetGroup(Node targetGroup) {
		this.label = targetGroup.valueOf("@label");
		this.range = new Integer(targetGroup.valueOf("@range"));

		this.targets = new ArrayList<>();
		for (Iterator it = targetGroup.selectNodes("Target").iterator(); it.hasNext();) {
			Node target = (Node) it.next();
			this.targets.add(new Target(target));
		}

		this.save();
	}

	@Override
	public String toString() {
		return label;
	}

	public Element toXML() {
		Element targetGroupElement = DocumentHelper.createElement(this.getClass().getSimpleName());
		targetGroupElement.addAttribute("label", label);
		targetGroupElement.addAttribute("range", String.format("%d", range));
		for (Target target : targets) {
			targetGroupElement.add(target.toXML());
		}
		return targetGroupElement;
	}

	public void deleteTarget(long targetID) {
		Target target = Target.findById(targetID);
		if (target != null && this.targets.contains(target)) {
			this.targets.remove(target);
			this.save();
			target.delete();
		}
	}

	public void deleteTargets() {
		ArrayList<Long> ids = new ArrayList<>();
		for (Target item : this.targets) {
			ids.add(item.id);
		}
		for (long item : ids) {
			deleteTarget(item);
		}
		this.targets = null;
		this.save();
	}
}