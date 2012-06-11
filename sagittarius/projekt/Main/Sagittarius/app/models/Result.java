package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Result extends Model {

	//TODO: add stage id or index to ensure correctness
	@ManyToOne
	public Stage stage;
	public int hits;
	public int targets;
	public int points;

	public Result(int hits, int targets, int points, Stage stage) {
		this.hits = hits;
		this.targets = targets;
		this.points = points;
		this.stage = stage;
	}

	@Override
	public String toString() {
		return String.format("%d/%d (%d)", hits, targets, points);
	}

	public Element toXML() {
		Element resultElement = DocumentHelper.createElement(this.getClass().getSimpleName());
		resultElement.addAttribute("stageindex", String.format("%d", stage.stageIndex));
		resultElement.addAttribute("hits", String.format("%d", hits));
		resultElement.addAttribute("targets", String.format("%d", targets));
		resultElement.addAttribute("points", String.format("%d", points));
		return resultElement;
	}
}