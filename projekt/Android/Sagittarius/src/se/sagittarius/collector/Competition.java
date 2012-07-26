package se.sagittarius.collector;

import org.simpleframework.xml.Attribute;

/**
 *
 * @author johan
 */
public class Competition {

	private int id;
	private String label;

	public Competition(@Attribute(name = "id") int id, @Attribute(name = "label") String label) {
		this.id = id;
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}

	@Attribute(name = "id")
	public int getId() {
		return id;
	}

	@Attribute(name = "label")
	public String getLabel() {
		return label;
	}
}