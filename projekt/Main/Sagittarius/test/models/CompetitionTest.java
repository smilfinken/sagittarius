package models;

import java.util.List;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 *
 * @author johan
 */
public class CompetitionTest extends UnitTest {

	List<Competition> competitions;

	public CompetitionTest() {
		Fixtures.deleteDatabase();
		Fixtures.loadModels("models/CompetitionTestData.yml");

		competitions = Competition.all().fetch();
	}

	@Test
	public void AddDeleteCompetitionTest() {
		int competitionCount = competitions.size();

		Competition competition = new Competition("Testtävling 1");
		competition.save();
		competitions = Competition.all().fetch();
		assertEquals(String.format("Number of competitions after adding one (%d) is incorrect.", competitions.size()), competitionCount + 1, competitions.size());

		competition.delete();
		competitions = Competition.all().fetch();
		assertEquals(String.format("Number of competitions after deleting one (%d) is incorrect.", competitions.size()), competitionCount, competitions.size());
	}

	@Test
	public void SetCompetitionTypeTest() {
		CompetitionType competitionType = CompetitionType.all().first();

		Competition competition = new Competition("Testtävling 1", competitionType);
		assertEquals(String.format("Competition type is not correctly set after 'new Competition()'"), competitionType, competition.competitionType);

		competition.delete();
		competition = new Competition("Testtävling 2");
		assertEquals(String.format("Competition type is not unset after 'new Competition()'"), null, competition.competitionType);

		competition.competitionType = competitionType;
		assertEquals(String.format("Competition type is not correctly set after setting competitionType"), competition.competitionType, competitionType);
		assertEquals(String.format("Competition type name is not correctly reported by getType()"), competitionType.label, competition.getType());
	}
}