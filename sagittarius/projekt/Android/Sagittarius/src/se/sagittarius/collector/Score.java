package se.sagittarius.collector;

/**
 *
 * @author johan
 */
public class Score {

	public int hits;
	public int targets;
	public int points;
	public boolean scored;

	public Score() {
		this.hits = 0;
		this.targets = 0;
		this.points = 0;
		this.scored = false;
	}

	public Score(int hits, int targets, int points) {
		this.hits = hits;
		this.targets = targets;
		this.points = points;
		this.scored = true;
	}
}