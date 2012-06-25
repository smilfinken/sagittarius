package models;

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
	@ManyToOne
	public Squad squad;
	public int squadIndex;

	public Competitor(User user) {
		this.user = user;
		this.results = null;
		this.squadIndex = 0;
	}

	public Competitor(User user, Division division) {
		this.user = user;
		this.division = division;
		this.results = null;
		this.squadIndex = 0;
	}

	public Competitor(User user, Division division, List<Result> results) {
		this.user = user;
		this.division = division;
		this.results = results;
		this.squadIndex = 0;

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