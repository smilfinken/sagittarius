package se.sagittarius.collector;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author johan
 */
@Root
public class Score {

	private int hits;
	private int targets;
	private int points;
	private boolean scored;

	public Score(@Attribute(name = "hits") int hits, @Attribute(name = "points") int targets, @Attribute(name = "points") int points) {
		this.hits = hits;
		this.targets = targets;
		this.points = points;
		this.scored = true;
	}

	@Attribute
	public int getHits() {
		return hits;
	}

	@Attribute
	public int getTargets() {
		return targets;
	}

	@Attribute
	public int getPoints() {
		return points;
	}

	public boolean isScored() {
		return scored;
	}
}