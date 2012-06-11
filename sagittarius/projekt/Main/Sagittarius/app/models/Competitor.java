package models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Competitor extends Model {

	@OneToOne
	public User user;
	@OneToOne
	public Division division;
	@OneToMany(cascade = CascadeType.ALL)
	public List<Result> results;

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

	@Override
	public String toString() {
		return user.getFullName();
	}

	public Element toXML() {
		Element competitorElement = DocumentHelper.createElement(this.getClass().getSimpleName());
		competitorElement.addAttribute("user", user.toString());
		competitorElement.addAttribute("division", division.toString());
		for (Result result : results) {
			competitorElement.add(result.toXML());
		}
		return competitorElement;
	}

	public String getDivisionAsString() {
		if (division != null) {
			if (division.ranks) {
				return String.format("%s%s", division.label, user.rank.ranking);
			} else {
				return String.format("%s", division.label);
			}
		} else {
			return "";
		}
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

	public void deleteResult(long resultID) {
		Result result = Result.findById(resultID);
		if (result != null && this.results.contains(result)) {
			this.results.remove(result);
			this.save();
			result.delete();
		}
	}

	public void deleteResults() {
		ArrayList<Long> ids = new ArrayList<>();
		for (Result item : this.results) {
			ids.add(item.id);
		}
		for (long item : ids) {
			deleteResult(item);
		}
		this.results = null;
		this.save();
	}
}