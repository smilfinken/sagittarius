package se.sagittarius.collector;

import java.util.List;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 * Container class that only handles the parsing of an element list
 *
 * @author johan
 */
@Element(name = "squads")
public class SquadContainer {

	@ElementList
	public List<Squad> squads;
}
