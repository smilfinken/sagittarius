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

	public Score() {
		this.hits = 0;
		this.targets = 0;
		this.points = 0;
		this.scored = false;
	}

	public Score(@Attribute(name = "hits") int hits, @Attribute(name = "targets") int targets, @Attribute(name = "points") int points) {
		this.hits = hits;
		this.targets = targets;
		this.points = points;
		//TODO: fix flag so it actually works, maybe?
		this.scored = true;
	}

	@Attribute(name = "hits", required = false)
	public int getHits() {
		return hits;
	}

	@Attribute(name = "targets", required = false)
	public int getTargets() {
		return targets;
	}

	@Attribute(name = "points", required = false)
	public int getPoints() {
		return points;
	}

	public boolean isScored() {
		return scored;
	}
}