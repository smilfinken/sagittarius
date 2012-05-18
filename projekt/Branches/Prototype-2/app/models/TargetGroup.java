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

	public String description;
	@OneToMany(cascade = CascadeType.ALL)
	public List<Target> targets;

	public TargetGroup() {
		this.description = "";
		this.targets = new ArrayList<>();
	}

	public TargetGroup(List<Target> targets) {
		this.description = "";
		this.targets = targets;
	}

	public TargetGroup(String description, List<Target> targets) {
		this.description = description;
		this.targets = targets;
	}
}