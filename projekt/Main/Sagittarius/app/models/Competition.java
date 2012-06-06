package models;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
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
	public Date date;
	@OneToOne
	public CompetitionType competitionType;
	@OneToOne
	public ScoringType scoringType;
	@OneToMany(cascade = CascadeType.ALL)
	public List<Stage> stages;
	@OneToMany(cascade = CascadeType.ALL)
	public List<Competitor> competitors;

	public Competition(String name) {
		this.name = name;
		this.date = new Date();
		this.competitionType = null;
		this.stages = null;
	}

	public Competition(String name, CompetitionType type) {
		this.name = name;
		this.date = new Date();
		this.competitionType = type;
		this.stages = null;
	}

	public Competition(String name, CompetitionType type, List<Stage> stages) {
		this.name = name;
		this.date = new Date();
		this.competitionType = type;
		this.stages = stages;
	}

	public Competition(String name, Date date, CompetitionType type, List<Stage> stages) {
		this.name = name;
		this.date = date;
		this.competitionType = type;
		this.stages = stages;
	}

	public String getType() {
		if (competitionType != null) {
			return competitionType.label;
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

	public int getMaxScore() {
		int maxScore = 0;
		if (stages != null) {
			int targets = 0;
			for (Stage stage : stages) {
				for (TargetGroup targetGroup : stage.targetGroups) {
					targets += targetGroup.targets.size();
				}
			}
			maxScore = targets + stages.size() * 6;
		}
		return maxScore;
	}

	public List<Competitor> getScoredCompetitors() {
		List<Competitor> scoredCompetitors = new ArrayList<>();

		for (Competitor competitor : competitors) {
			if (competitor.isScored()) {
				scoredCompetitors.add(competitor);
			}
		}

		return scoredCompetitors;
	}

	public String getDate() {
		String shortDate = "";

		if (date != null) {
			shortDate = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
		}
		return shortDate;
	}
}