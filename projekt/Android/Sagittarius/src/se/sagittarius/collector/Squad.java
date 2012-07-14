package se.sagittarius.collector;

import java.util.ArrayList;
import java.util.Arrays;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 * @author johan
 */
@Root(name = "squad")
public class Squad {

	private Competitor[] competitors;

	Squad(Competitor[] competitors) {
		this.competitors = competitors;
	}

	public Squad(@ElementList(name = "competitors") ArrayList<Competitor> competitors) {
		this.competitors = (Competitor[]) competitors.toArray();
	}

	@ElementList(name = "competitors")
	private ArrayList<Competitor> getCompetitorsArray() {
		return new ArrayList<Competitor>(Arrays.asList(competitors));
	}
}
