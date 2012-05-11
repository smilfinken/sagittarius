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

		assertTrue(String.format("Competitor Young Shooter in division J is indicated as %s", competitorJuniorJ.getDivision()), "J".equals(competitorJuniorJ.getDivision()));
		assertTrue(String.format("Competitor Young Shooter in division A is indicated as %s", competitorJuniorA.getDivision()), "A1".equals(competitorJuniorA.getDivision()));
		assertTrue(String.format("Competitor Sharp Shooter in division C is indicated as %s", competitorAdultC.getDivision()), "C2".equals(competitorAdultC.getDivision()));
		assertTrue(String.format("Competitor Sharp Shooter in division A is indicated as %s", competitorAdultA.getDivision()), "A2".equals(competitorAdultA.getDivision()));
		assertTrue(String.format("Competitor Girl Shooter in division D is indicated as %s", competitorLadyD.getDivision()), "D3".equals(competitorLadyD.getDivision()));
		assertTrue(String.format("Competitor Girl Shooter in division A is indicated as %s", competitorLadyA.getDivision()), "A3".equals(competitorLadyA.getDivision()));
		assertTrue(String.format("Competitor Girl Shooter in division V is indicated as %s", competitorLadySeniorV.getDivision()), "VY".equals(competitorLadySeniorV.getDivision()));
		assertTrue(String.format("Competitor Old Shooter in division V is indicated as %s", competitorSeniorV.getDivision()), "VY".equals(competitorSeniorV.getDivision()));
		assertTrue(String.format("Competitor Old Shooter in division A is indicated as %s", competitorSeniorA.getDivision()), "A3".equals(competitorSeniorA.getDivision()));
	}

	@Test
	public void NameTest() {
		Competitor competitorJuniorJ = competitors.get(0);
		Competitor competitorAdultA = competitors.get(3);
		Competitor competitorLadyD = competitors.get(4);
		Competitor competitorSeniorV = competitors.get(7);

		assertTrue(String.format("Competitor Young Shooter is erroneously referred to as %s", competitorJuniorJ.getFullName()), "Young Shooter".equals(competitorJuniorJ.getFullName()));
		assertTrue(String.format("Competitor Sharp Shooter is erroneously referred to as %s", competitorAdultA.getFullName()), "Sharp Shooter".equals(competitorAdultA.getFullName()));
		assertTrue(String.format("Competitor Girl Shooter is erroneously referred to as %s", competitorLadyD.getFullName()), "Girl Shooter".equals(competitorLadyD.getFullName()));
		assertTrue(String.format("Competitor Old Shooter is erroneously referred to as %s", competitorSeniorV.getFullName()), "Old Shooter".equals(competitorSeniorV.getFullName()));
	}
}