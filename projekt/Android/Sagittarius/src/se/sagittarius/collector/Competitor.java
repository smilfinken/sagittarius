package se.sagittarius.collector;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 * @author johan
 */
@Root
public class Competitor {

	private String name;
	private Score[] scores;

	public Competitor(String name, int stageCount) {
		this.name = name;
		this.scores = new Score[stageCount];
	}

	public Competitor(@Attribute(name = "name") String name, @ElementList(name = "scores") Score[] scores) {
		this.name = name;
		this.scores = scores;
	}

	@Attribute(name = "name")
	public String getName() {
		return name;
	}

	@ElementList(name = "scores")
	public Score[] getScores() {
		return scores;
	}
}