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

	private String name;
	private Score[] scores;

	public Competitor(String name, int stageCount) {
		this.name = name;
		this.scores = new Score[stageCount];
	}

	public Competitor(@Attribute(name = "name") String name, Score[] scores) {
		this.name = name;
		this.scores = scores;
	}

	public Competitor(@Attribute(name = "name") String name, @ElementList(name = "scores") ArrayList<Score> scores) {
		this.name = name;
		this.scores = new Score[scores.size()];
		this.scores = (Score[]) scores.toArray();
	}

	@Attribute(name = "name")
	public String getName() {
		return name;
	}

	@ElementList(name = "scores")
	public ArrayList<Score> getScoresArray() {
		return new ArrayList<Score>(Arrays.asList(scores));
	}

	public Score[] getScores() {
		return scores;
	}
}