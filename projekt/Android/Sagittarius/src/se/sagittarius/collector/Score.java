package se.sagittarius.collector;

import org.simpleframework.xml.Attribute;

/**
 *
 * @author johan
 */
public class Score {

	private int hits;
	private int targets;
	private int points;
	private boolean scored;

	public Score(@Attribute(name = "hits") int hits, @Attribute(name = "targets") int targets, @Attribute(name = "points") int points) {
		this.hits = hits;
		this.targets = targets;
		this.points = points;
		this.scored = true;
	}

	@Attribute(name = "hits")
	public int getHits() {
		return hits;
	}

	@Attribute(name = "targets")
	public int getTargets() {
		return targets;
	}

	@Attribute(name = "points")
	public int getPoints() {
		return points;
	}

	public boolean isScored() {
		return scored;
	}
}