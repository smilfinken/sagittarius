package se.sagittarius.collector;

import java.util.ArrayList;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

/**
 *
 * @author johan
 */
public class Squad {

	private long id;
	private String label;
	private ArrayList<Competitor> competitors;

	public Squad(@Attribute(name = "id") long id, @Attribute(name = "label") String label, @ElementList(name = "competitors", required = false, empty = true) ArrayList<Competitor> competitors) {
		this.id = id;
		this.label = label;
		this.competitors = competitors;
	}

	@Override
	public String toString() {
		return label;
	}

	@ElementList(name = "competitors", required = false)
	public ArrayList<Competitor> getCompetitors() {
		return competitors;
	}

	@Attribute(name = "id", required = false)
	public long getId() {
		return id;
	}

	@Attribute(name = "label", required = false)
	public String getLabel() {
		return label;
	}

	public boolean isScored() {
		boolean result = (competitors != null && !competitors.isEmpty());

		for (Competitor competitor : competitors) {
			result = result && competitor.isScored();
		}

		return result;
	}
}