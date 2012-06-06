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
}