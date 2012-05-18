package models;

import java.util.List;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 *
 * @author johan
 */
public class CompetitorTest extends UnitTest {

	List<Competitor> competitors;

	public CompetitorTest() {
		Fixtures.deleteDatabase();
		Fixtures.loadModels("models/CompetitorTestData.yml");

		competitors = Competitor.findAll();
	}

	@Test
	public void DivisionTest() {
		Competitor competitorJuniorJ = competitors.get(0);
		Competitor competitorJuniorA = competitors.get(1);
		Competitor competitorAdultC = competitors.get(2);
		Competitor competitorAdultA = competitors.get(3);
		Competitor competitorLadyD = competitors.get(4);
		Competitor competitorLadyA = competitors.get(5);
		Competitor competitorLadySeniorV = competitors.get(6);
		Competitor competitorSeniorV = competitors.get(7);
		Competitor competitorSeniorA = competitors.get(8);

		assertEquals(String.format("Competitor Young Shooter in division J is indicated as %s", competitorJuniorJ.getDivision()), "J", competitorJuniorJ.getDivision());
		assertEquals(String.format("Competitor Young Shooter in division A is indicated as %s", competitorJuniorA.getDivision()), "A1", competitorJuniorA.getDivision());
		assertEquals(String.format("Competitor Sharp Shooter in division C is indicated as %s", competitorAdultC.getDivision()), "C2", competitorAdultC.getDivision());
		assertEquals(String.format("Competitor Sharp Shooter in division A is indicated as %s", competitorAdultA.getDivision()), "A2", competitorAdultA.getDivision());
		assertEquals(String.format("Competitor Girl Shooter in division D is indicated as %s", competitorLadyD.getDivision()), "D3", competitorLadyD.getDivision());
		assertEquals(String.format("Competitor Girl Shooter in division A is indicated as %s", competitorLadyA.getDivision()), "A3", competitorLadyA.getDivision());
		assertEquals(String.format("Competitor Girl Shooter in division V is indicated as %s", competitorLadySeniorV.getDivision()), "VY", competitorLadySeniorV.getDivision());
		assertEquals(String.format("Competitor Old Shooter in division V is indicated as %s", competitorSeniorV.getDivision()), "VY", competitorSeniorV.getDivision());
		assertEquals(String.format("Competitor Old Shooter in division A is indicated as %s", competitorSeniorA.getDivision()), "A3", competitorSeniorA.getDivision());
	}

	@Test
	public void NameTest() {
		Competitor competitorJuniorJ = competitors.get(0);
		Competitor competitorAdultA = competitors.get(3);
		Competitor competitorLadyD = competitors.get(4);
		Competitor competitorSeniorV = competitors.get(7);

		assertEquals(String.format("Competitor Young Shooter is erroneously referred to as %s", competitorJuniorJ.getFullName()), "Young Shooter", competitorJuniorJ.getFullName());
		assertEquals(String.format("Competitor Sharp Shooter is erroneously referred to as %s", competitorAdultA.getFullName()), "Sharp Shooter", competitorAdultA.getFullName());
		assertEquals(String.format("Competitor Girl Shooter is erroneously referred to as %s", competitorLadyD.getFullName()), "Girl Shooter", competitorLadyD.getFullName());
		assertEquals(String.format("Competitor Old Shooter is erroneously referred to as %s", competitorSeniorV.getFullName()), "Old Shooter", competitorSeniorV.getFullName());
	}
}