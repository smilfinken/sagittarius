package models;

import java.util.Iterator;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.joda.time.DateTime;
import play.db.jpa.Model;
import play.i18n.Messages;

/**
 *
 * @author johan
 */
@Entity
public class Squad extends Model {

	public int squadIndex;
	public int slots;
	@OneToMany
	public List<Competitor> competitors;

	public Squad(int squadIndex) {
		this.squadIndex = squadIndex;
		this.slots = -1;
	}

	public Squad(int squadIndex, int slots) {
		this.squadIndex = squadIndex;
		this.slots = slots;
	}

	public Squad(Node squad) {
		this.squadIndex = new Integer(squad.valueOf("@squadindex"));
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
		return String.format("%s %d", Messages.get("squad.label"), squadIndex);
	}

	public Element toXML() {
		Element userElement = DocumentHelper.createElement(this.getClass().getSimpleName());
		userElement.addAttribute("squadindex", String.format("%d", squadIndex));
		userElement.addAttribute("slots", String.format("%d", slots));

		for (Competitor competitor : competitors) {
			userElement.add(competitor.toXML());
		}
		return userElement;
	}
}