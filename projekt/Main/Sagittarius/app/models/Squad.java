package models;

import common.Formatting;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.persistence.*;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import play.db.jpa.Model;
import play.i18n.Messages;

/**
 *
 * @author johan
 */
@Entity
public class Squad extends Model {

	public String label;
	public Date startTime;
	@Column(nullable = false)
	public int squadNumber;
	public int slots;
	@OneToMany
	//@OrderBy(value = "squadIndex")
	public List<Competitor> competitors;

	public Squad(int squadIndex) {
		this.squadNumber = squadIndex;
		this.slots = -1;
	}

	public Squad(String label, int squadIndex) {
		this.label = label;
		this.squadNumber = squadIndex;
		this.slots = -1;
	}

	public Squad(String label, int squadIndex, int slots) {
		this.label = label;
		this.squadNumber = squadIndex;
		this.slots = slots;
	}

	public Squad(String label, int squadIndex, int slots, String time) {
		this.label = label;
		this.squadNumber = squadIndex;
		this.slots = slots;
		this.startTime = Formatting.timeStringToDate(time);
	}

	public Squad(Node squad) {
		this.label = squad.valueOf("@label");
		this.squadNumber = new Integer(squad.valueOf("@squadnumber"));
		this.slots = new Integer(squad.valueOf("@slots"));
		this.competitors = new ArrayList<>();
		for (Iterator it = squad.selectNodes("Competitor").iterator(); it.hasNext();) {
			Node competitorNode = (Node) it.next();
			User user = User.find("byEmail", competitorNode.valueOf("@user")).first();
			Competitor competitor = Competitor.find("byUser", user).first();
			this.competitors.add(competitor);
		}
		this.save();
	}

	@Override
	public String toString() {
		if (label != null && label.length() != 0) {
			return String.format("%s %d (%s)", Messages.get("squad.label"), squadNumber, label);
		} else {
			return String.format("%s %d", Messages.get("squad.label"), squadNumber);
		}
	}

	public Element toXML() {
		Element userElement = DocumentHelper.createElement(this.getClass().getSimpleName());
		userElement.addAttribute("label", label);
		userElement.addAttribute("squadnumber", String.format("%d", squadNumber));
		userElement.addAttribute("slots", String.format("%d", slots));
		for (Competitor competitor : competitors) {
			userElement.add(competitor.toXML());
		}
		return userElement;
	}

	public Competitor addCompetitor(Competitor competitor) {
		if (competitor != null) {
			this.competitors.add(competitor);
			this.save();

			competitor.squad = this;
			competitor.save();
		}
		return competitor;
	}

	public Competitor removeCompetitor(Competitor competitor) {
		if (competitor != null && this.competitors.contains(competitor)) {
			this.competitors.remove(competitor);
			this.save();

			competitor.squad = null;
			competitor.save();
		}
		return competitor;
	}

	public void removeCompetitors() {
		for (Competitor item : competitors) {
			item.squad = null;
		}
		this.competitors.removeAll(this.competitors);
	}

	public String getStartTimeAsString() {
		String result = "";

		if (startTime != null) {
			result = Formatting.dateToStartTime(startTime);
		}

		return result;
	}
}
