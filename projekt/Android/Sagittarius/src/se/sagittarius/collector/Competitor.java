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

	private long id;
	private int position;
	private String name;
	private Score[] scores;

	public Competitor(long id, int position, String name, int stageCount) {
		this.id = id;
		this.position = position;
		this.name = name;
		this.scores = new Score[stageCount];
		for (int i = 0; i < stageCount; i++) {
			scores[i] = new Score();
		}
	}

	public Competitor(@Attribute(name = "id") long id, @Attribute(name = "position") int position, @Attribute(name = "name") String name, @ElementList(name = "scores", required = false, empty = true) ArrayList<Score> scores) {
		this.id = id;
		this.position = position;
		this.name = name;
		if (scores != null) {
			this.scores = scores.toArray(new Score[scores.size()]);
		}
	}

	@Override
	public String toString() {
		return String.format("%d. %s", id, name);
	}

	@Attribute(name = "id")
	public long getId() {
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

	public boolean setScore(int stageIndex, Score score) {
		boolean result = false;
		if (scores != null && scores.length >= stageIndex + 1) {
			scores[stageIndex] = score;
			result = true;
		}
		return result;
	}

	public boolean isScored(){
		boolean result = (scores != null && scores.length !=0);

		for (Score score : scores){
			result = result && score.isScored();
		}

		return result;
	}
}