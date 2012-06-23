package models;

import static common.Sorting.sortCompetitors;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.*;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
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
	@OrderBy(value = "stageIndex")
	public List<Stage> stages;
	@OneToMany(cascade = CascadeType.ALL)
	public List<Competitor> competitors;
	@OneToMany
	public List<Squad> squads;

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

	public Competition(Node competition) throws ParseException {
		this.label = competition.valueOf("@label");
		this.date = new SimpleDateFormat("yyyy-MM-dd", new Locale("sv", "SE")).parse(competition.valueOf("@date"));
		this.scoringType = ScoringType.find("byLabel", competition.valueOf("@scoringtype")).first();
		this.competitionType = CompetitionType.find("byLabel", competition.valueOf("@competitiontype")).first();

		this.competitors = new ArrayList<>();
		for (Iterator it = competition.selectNodes("Competitor").iterator(); it.hasNext();) {
			Node competitor = (Node) it.next();
			this.competitors.add(new Competitor(competitor));
		}

		this.stages = new ArrayList<>();
		for (Iterator it = competition.selectNodes("Stage").iterator(); it.hasNext();) {
			Node stage = (Node) it.next();
			this.stages.add(new Stage(stage));
		}

		this.save();
	}

	@Override
	public String toString() {
		return label;
	}

	public Element toXML() {
		//TODO: finish the importing routine for all sub-elements
		Element competitionElement = DocumentHelper.createElement(this.getClass().getSimpleName());
		competitionElement.addAttribute("label", label);
		competitionElement.addAttribute("date", getDate());
		competitionElement.addAttribute("competitiontype", competitionType.toString());
		competitionElement.addAttribute("scoringtype", scoringType.toString());

		for (Iterator<Competitor> it = sortCompetitors(competitors).iterator(); it.hasNext();) {
			Competitor competitor = it.next();
			competitionElement.add(competitor.toXML());
		}

		for (Iterator<Stage> it = stages.iterator(); it.hasNext();) {
			Stage stage = it.next();
			competitionElement.add(stage.toXML());
		}

		//for (Iterator<Squad> it = squads.iterator(); it.hasNext();) {
		//Squad squad = it.next();
		//competitionElement.add(squad.toXML());
		//}

		return competitionElement;
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
				nextID = getNextStage(nextID);
				if (nextStage != null) {
					nextStage.stageIndex--;
					nextStage.save();
				}
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
		if (competitor != null) {
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
				if (competitor.user != null && competitor.user.id == user.id) {
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
				if (competitor.user != null && competitor.user.id == user.id && competitor.division.id == divisionID) {
					result = true;
				}
			}
		}

		return result;
	}

	public int getMaxScore() {
		return getMaxScore(6);
	}

	public int getMaxScore(int shotsPerStage) {
		int result = 0;

		if (stages != null) {
			int targets = 0;
			for (Stage stage : stages) {
				for (TargetGroup targetGroup : stage.targetGroups) {
					targets += Math.min(shotsPerStage, targetGroup.targets.size());
				}
			}
			result = targets + stages.size() * shotsPerStage;
		}

		return result;
	}

	public int getTopScore() {
		int result = 0;

		for (Competitor competitor : competitors) {
			result = Math.max(result, competitor.getScore());
		}

		return result;
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

	public int CountCompetitors() {
		int result = 0;

		result = getScoredCompetitors().size();

		return result;
	}

	public int CountUsers() {
		int result = 0;

		List<User> users = new ArrayList<>();
		for (Competitor competitor : getScoredCompetitors()) {
			if (!users.contains(competitor.user)) {
				users.add(competitor.user);
			}
		}
		result = users.size();

		return result;
	}
}