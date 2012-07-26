package se.sagittarius.collector;

import java.util.ArrayList;
import java.util.Arrays;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

/**
 *
 * @author johan
 */
public class Squad {

	private int id;
	private String label;
	private ArrayList<Competitor> competitors;

	public Squad(Competitor[] competitors) {
		this.id = -1;
		this.label = "dummy";
		this.competitors = new ArrayList<Competitor>(Arrays.asList(competitors));
	}

	public Squad(@Attribute(name = "id") int id, @Attribute(name = "label") String label, @ElementList(name = "competitors", required = false, empty = true) ArrayList<Competitor> competitors) {
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
	public int getId() {
		return id;
	}

	@Attribute(name = "label", required = false)
	public String getLabel() {
		return label;
	}
}