package models;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Competition extends Model {

	public String name;
	@OneToOne
	public CompetitionType competitionType;
	@OneToMany(cascade = CascadeType.ALL)
	public List<Stage> stages;
	@OneToMany(cascade = CascadeType.ALL)
	public List<Competitor> competitors;

	public Competition(String name) {
		this.name = name;
		this.competitionType = null;
		this.stages = null;
		this.save();
	}

	public Competition(String name, CompetitionType type) {
		this.name = name;
		this.competitionType = type;
		this.stages = null;
		this.save();
	}

	public Competition(String name, CompetitionType type, List<Stage> stages) {
		this.name = name;
		this.competitionType = type;
		this.stages = stages;
		this.save();
	}

	public String getType() {
		if (competitionType != null) {
			return competitionType.name;
		} else {
			return "";
		}
	}

	public boolean hasStages() {
		if (competitionType != null) {
			return competitionType.hasStages();
		} else {
			return false;
		}
	}
}