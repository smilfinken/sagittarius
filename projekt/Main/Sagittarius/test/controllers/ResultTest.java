package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.*;
import org.junit.Test;
import play.test.UnitTest;

public class ResultTest extends UnitTest {

	@Test
	public void ResultComparatorTest() {
		User userFirst = new User("First", "Shooter", "first@shooter");
		User userSecond = new User("Second", "Shooter", "second@shooter");
		User userThird = new User("Third", "Shooter", "third@shooter");
		User userFourth = new User("Fourth", "Shooter", "fourth@shooter");

		Category category = new Category("J");
		userFirst.categories = Arrays.asList(category);
		userSecond.categories = Arrays.asList(category);
		userThird.categories = Arrays.asList(category);
		userFourth.categories = Arrays.asList(category);

		Rank rank = new Rank(1);
		userFirst.rank = rank;
		userSecond.rank = rank;
		userThird.rank = rank;
		userFourth.rank = rank;

		Division division = new Division(Arrays.asList(new CompetitionType("Testtävling", true)), "J", Arrays.asList(category), false);
		Competitor competitorFirst = new Competitor(userFirst, division, mockResults(new int[]{6, 6, 6, 6, 6, 6}, new int[]{4, 3, 2, 1, 6, 4}, new int[]{0, 0, 0, 0, 25, 15}));
		Competitor competitorSecond = new Competitor(userSecond, division, mockResults(new int[]{6, 6, 6, 6, 6, 6}, new int[]{4, 3, 2, 1, 6, 4}, new int[]{0, 0, 0, 0, 25, 14}));
		Competitor competitorThird = new Competitor(userThird, division, mockResults(new int[]{5, 6, 6, 6, 6, 6}, new int[]{4, 3, 2, 1, 6, 4}, new int[]{0, 0, 0, 0, 25, 15}));
		Competitor competitorFourth = new Competitor(userFourth, division, mockResults(new int[]{5, 6, 6, 6, 6, 6}, new int[]{3, 3, 2, 1, 6, 4}, new int[]{0, 0, 0, 0, 25, 14}));

		List<Competitor> competitors = new ArrayList<>(4);
		competitors.add(competitorFourth);
		competitors.add(competitorThird);
		competitors.add(competitorSecond);
		competitors.add(competitorFirst);

		List<Competitor> sortedCompetitors = common.Sorting.sortResults(competitors);
		assertTrue(sortedCompetitors.indexOf(competitorFirst) == 0);
		assertTrue(sortedCompetitors.indexOf(competitorSecond) == 1);
		assertTrue(sortedCompetitors.indexOf(competitorThird) == 2);
		assertTrue(sortedCompetitors.indexOf(competitorFourth) == 3);
	}

	private List<Result> mockResults(int[] hits, int[] targets, int[] points) {
		assertTrue(
			"Failed due to array length discrepancy, result-target-points must be of equal length",
			hits.length == targets.length
			&& targets.length == points.length);
		List<Result> results = new ArrayList<>();
		for (int i = 0; i < hits.length; i++) {
			results.add(new Result(hits[i], targets[i], points[i], 0));
		}
		return results;
	}
}