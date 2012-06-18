package models;

import javax.persistence.Entity;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Result extends Model {

	//TODO: add stage id or index to ensure correctness
	public int stageIndex;
	public int hits;
	public int targets;
	public int points;

	public Result(int hits, int targets, int points, int stageIndex) {
		this.hits = hits;
		this.targets = targets;
		this.points = points;
		this.stageIndex = stageIndex;
	}

	public Result(Node result) {
		this.stageIndex = new Integer(result.valueOf("@stageindex"));
		this.hits = new Integer(result.valueOf("@hits"));
		this.targets = new Integer(result.valueOf("@targets"));
		this.points = new Integer(result.valueOf("@points"));
	}

	@Override
	public String toString() {
		return String.format("%d/%d (%d)", hits, targets, points);
	}

	public Element toXML() {
		Element resultElement = DocumentHelper.createElement(this.getClass().getSimpleName());
		resultElement.addAttribute("stageindex", String.format("%d", stageIndex));
		resultElement.addAttribute("hits", String.format("%d", hits));
		resultElement.addAttribute("targets", String.format("%d", targets));
		resultElement.addAttribute("points", String.format("%d", points));
		return resultElement;
	}
}