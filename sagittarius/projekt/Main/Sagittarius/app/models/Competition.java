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

	public String label;
	public Date date;
	@OneToOne
	public CompetitionType competitionType;
	@OneToOne
	public ScoringType scoringType;
	@OneToMany(cascade = CascadeType.ALL)
	public List<Stage> stages;
	@OneToMany(cascade = CascadeType.ALL)
	public List<Competitor> competitors;

	public Competition(String label) {
		this.label = label;
		this.date = new Date();
		this.competitionType = null;
		this.stages = null;
	}

	public Competition(String label, CompetitionType type) {
		this.label = label;
		this.date = new Date();
		this.competitionType = type;
		this.stages = null;
	}

	public Competition(String label, CompetitionType type, List<Stage> stages) {
		this.label = label;
		this.date = new Date();
		this.competitionType = type;
		this.stages = stages;
	}

	public Competition(String label, Date date, CompetitionType type, List<Stage> stages) {
		this.label = label;
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
		return (stages != null && stages.size() > 0);
	}

	public void deleteStage(long stageID) {
		Stage stage = Stage.findById(stageID);
		if (stage != null && this.stages.contains(stage)) {
			stage.deleteTargetGroups();

			long nextID = getNextStage(stageID);
			while (nextID != -1) {
				Stage nextStage = Stage.findById(nextID);
				if (nextStage != null) {
					nextStage.stageIndex--;
					nextStage.save();
				}
				nextID = getNextStage(nextID);
			}
			this.stages.remove(stage);
			this.save();
			stage.delete();
		}
	}

	public void deleteStages() {
		ArrayList<Long> ids = new ArrayList<>();
		for (Stage item : this.stages) {
			ids.add(item.id);
		}
		for (long item : ids) {
			deleteStage(item);
		}
		this.stages = null;
		this.save();
	}

	public void deleteCompetitor(long competitorID) {
		Competitor competitor = Competitor.findById(competitorID);
		if (competitor != null){
			this.competitors.remove(competitor);
			this.save();
			competitor.deleteResults();
			competitor.delete();
		}
	}

	public void deleteCompetitors() {
		ArrayList<Long> ids = new ArrayList<>();
		for (Competitor item : this.competitors) {
			ids.add(item.id);
		}
		for (long item : ids) {
			deleteCompetitor(item);
		}
		this.competitors = null;
		this.save();
	}

	public boolean isEnrolled(String username) {
		boolean result = false;
		User user = User.find("byEmail", username).first();

		if (user != null) {
			for (Competitor competitor : this.competitors) {
				if (competitor.user.id == user.id) {
					result = true;
				}
			}
		}
		return result;
	}

	public boolean isEnrolled(String username, long divisionID) {
		boolean result = false;
		User user = User.find("byEmail", username).first();

		if (user != null) {
			for (Competitor competitor : this.competitors) {
				if (competitor.user.id == user.id && competitor.division.id == divisionID) {
					result = true;
				}
			}
		}
		return result;
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

	public boolean isFirstStage(long stageID) {
		boolean result = false;
		Stage stage = Stage.findById(stageID);

		if (stage != null) {
			result = (this.stages.contains(stage) && stage.stageIndex == 1);
		}

		return result;
	}

	public boolean isLastStage(long stageID) {
		boolean result = false;
		Stage stage = Stage.findById(stageID);

		if (stage != null) {
			result = (this.stages.contains(stage) && stage.stageIndex == this.stages.size());
		}

		return result;
	}

	public long getNextStage(long stageID) {
		long result = -1;
		Stage stage = Stage.findById(stageID);

		if (stage != null) {
			int index = stage.stageIndex;
			for (Stage candidate : this.stages) {
				if (candidate.stageIndex == index + 1) {
					result = candidate.id;
				}
			}
		}

		return result;
	}

	public long getPreviousStage(long stageID) {
		long result = -1;
		Stage stage = Stage.findById(stageID);

		if (stage != null) {
			int index = stage.stageIndex;
			for (Stage candidate : this.stages) {
				if (candidate.stageIndex == index - 1) {
					result = candidate.id;
				}
			}
		}

		return result;
	}
}