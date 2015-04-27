package models;

import common.Formatting;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
public class Competitor extends Model {

	@ManyToOne
	public User user;
	@ManyToOne
	public Division division;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "stageIndex")
	public List<Result> results;
	@ManyToOne(cascade = CascadeType.ALL)
	public Squad squad;

	public Competitor(User user) {
		this.user = user;
		this.results = null;
	}

	public Competitor(User user, Division division) {
		this.user = user;
		this.division = division;
		this.results = null;
	}

	public Competitor(User user, Division division, List<Result> results) {
		this.user = user;
		this.division = division;
		this.results = results;

		if (results != null) {
			for (Result result : results) {
				result.create();
			}
		}
	}

	public Competitor(Node competitor) {
		this.user = User.find("byEmail", competitor.valueOf("@user")).first();
		if (this.user == null) {
			this.user = new User(competitor.selectSingleNode("User"));
		}
		if (this.user != null) {
			this.division = Division.find("byLabel", competitor.valueOf("@division")).first();

			this.results = new ArrayList<>();
			for (Iterator it = competitor.selectNodes("Result").iterator(); it.hasNext();) {
				Node result = (Node) it.next();
				this.results.add(new Result(result));
			}

			this.save();
		}
	}

	@Override
	public String toString() {
		return user.getFullName();
	}

	public Element toXML() {
		Element competitorElement = DocumentHelper.createElement(this.getClass().getSimpleName());
		competitorElement.addAttribute("user", user.toString());
		competitorElement.addAttribute("division", division.toString());
		competitorElement.add(user.toXML());
		for (Iterator<Result> it = results.iterator(); it.hasNext();) {
			Result result = it.next();
			competitorElement.add(result.toXML());
		}
		return competitorElement;
	}

	public String getOrganisation() {
		String result = "";

		if (user.organisation != null) {
			result = String.format("%s", user.organisation.label);
		}

		return result;
	}

	public String getShortOrganisation() {
		String result = "";

		if (user.organisation != null) {
			result = String.format("%s", user.organisation.shortname);
		}

		return result;
	}

	public String getDivisionAsString() {
		String result = "";

		if (division != null) {
			if (division.ranks) {
				result = String.format("%s%s", division.label, user.rank.ranking);
			} else {
				result = String.format("%s", division.label);
			}
		}

		return result;
	}

	public boolean hasSquad() {
		return (squad != null);
	}

	public long getSquadId() {
		long result = -1;

		if (squad != null) {
			result = squad.id;
		}

		return result;
	}

	public int getSquadNumber() {
		int result = -1;

		if (squad != null) {
			result = squad.squadNumber;
		}

		return result;
	}

	public String getStartTime() {
		String result = "";

		if (squad != null) { // && squad.startTime != null) {
			result = squad.getStartTimeAsString();
			//result = Formatting.dateToStartTime(squad.startTime);
		}

		return result;
	}

	public boolean isScored() {
		if (results != null) {
			return (results.size() > 0);
		} else {
			return false;
		}
	}

	public int getScore() {
		int score = 0;

		for (Result result : results) {
			score += result.hits + result.targets;
		}

		return score;
	}

	public void addResults(List<Result> results) {
		deleteResults();
		this.results.addAll(results);
		this.save();
	}

	public void deleteResults() {
		this.results.removeAll(this.results);
		this.save();
	}
}
