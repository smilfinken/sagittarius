package se.sagittarius.collector;

import java.util.ArrayList;
import java.util.Arrays;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

/**
 *
 * @author johan
 */
public class Competitor {

	private int id;
	private int position;
	private String name;
	private Score[] scores;

	//public Competitor(@Attribute(name = "id") int id, @Attribute(name = "name") String name, @ElementList(name = "scores", required = false, empty = true) ArrayList<Score> scores) {
	public Competitor(@Attribute(name = "id") int id, @Attribute(name = "position") int position, @Attribute(name = "name") String name, @ElementList(name = "scores", required = false, empty = true) ArrayList<Score> scores) {
		this.id = id;
		this.position = position;
		this.name = name;
		//this.scores = scores.toArray(new Score[scores.size()]);
	}

	@Override
	public String toString() {
		return String.format("%d. %s", id, name);
	}

	@Attribute(name = "id")
	public int getId() {
		return id;
	}

	@Attribute(name = "position")
	public int getPosition() {
		return position;
	}

	@Attribute(name = "name")
	public String getName() {
		return name;
	}

	@ElementList(name = "scores", required = false, empty = true)
	public ArrayList<Score> getScores() {
		if (scores != null) {
			return new ArrayList<Score>(Arrays.asList(scores));
		} else {
			return null;
		}
	}

	public Score[] getScoresAsArray() {
		return scores;
	}
}