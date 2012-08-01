package se.sagittarius.collector;

import java.util.List;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 * Container class that only handles the parsing of an element list
 *
 * @author johan
 */
@Element(name = "stages")
public class StageContainer {

	@ElementList
	public List<Stage> stages;
}
