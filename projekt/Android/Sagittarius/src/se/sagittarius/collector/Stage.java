package se.sagittarius.collector;

import org.simpleframework.xml.Attribute;

/**
 *
 * @author johan
 */
public class Stage {

	private int stageIndex;
	private String label;
	private int targetCount;
	private boolean hasPoints;

	public Stage(@Attribute(name = "stageindex") int stageIndex, @Attribute(name = "label", required = false) String label, @Attribute(name = "targetcount") int targetCount, @Attribute(name = "haspoints") boolean hasPoints) {
		this.stageIndex = stageIndex;
		this.label = label;
		this.targetCount = targetCount;
		this.hasPoints = hasPoints;
	}

	@Override
	public String toString() {
		return String.format("Station %d. %s", stageIndex, label);
	}

	@Attribute(name = "stageindex")
	public int getStageIndex() {
		return this.stageIndex;
	}

	@Attribute(name = "label", required = false)
	public String getLabel() {
		return this.label;
	}

	@Attribute(name = "targetcount")
	public int getTargetCount() {
		return this.targetCount;
	}

	@Attribute(name = "haspoints")
	public boolean getHasPoints() {
		return this.hasPoints;
	}
}