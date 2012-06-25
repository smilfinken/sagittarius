package models;

import java.util.Iterator;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
	@Column(nullable = false)
	public int squadNumber;
	public int slots;
	@OneToMany(mappedBy = "squad")
	@OrderBy(value = "squadIndex")
	public List<Competitor> competitors;

	public Squad(int squadIndex) {
		this.squadNumber = squadIndex;
		this.slots = -1;
	}

	public Squad(String label, int squadIndex, int slots) {
		this.label = label;
		this.squadNumber = squadIndex;
		this.slots = slots;
	}

	public Squad(Node squad) {
		this.label = squad.valueOf("@label");
		this.squadNumber = new Integer(squad.valueOf("@squadnumber"));
		this.slots = new Integer(squad.valueOf("@slots"));
		for (Iterator it = squad.selectNodes("Competitor").iterator(); it.hasNext();) {
			Node competitorNode = (Node) it.next();
			Competitor competitor = Category.find("byLabel", competitorNode.valueOf("@label")).first();
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
		userElement.addAttribute("squadindex", String.format("%d", squadNumber));
		userElement.addAttribute("slots", String.format("%d", slots));

		for (Competitor competitor : competitors) {
			userElement.add(competitor.toXML());
		}
		return userElement;
	}
}