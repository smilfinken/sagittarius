package controllers;

import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import models.Category;
import models.Competitor;
import models.Rank;
import models.Result;
import models.User;

import org.junit.Test;

import play.test.UnitTest;

public class ResultTest extends UnitTest {

	@Test
	public void ResultComparatorTest() {
		User userFirst = new User("First", "Shooter");
		User userSecond = new User("Second", "Shooter");
		User userThird = new User("Third", "Shooter");
		User userFourth = new User("Fourth", "Shooter");
		
		userFirst.category = new Category("J", "Junior");
		userSecond.category = new Category("J", "Junior");
		userThird.category = new Category("J", "Junior");
		userFourth.category = new Category("J", "Junior");
		
		userFirst.rank = new Rank(1);
		userSecond.rank = new Rank(1);
		userThird.rank = new Rank(1);
		userFourth.rank = new Rank(1);
		
		Competitor competitorFirst = new Competitor(userFirst, mockResults(new int[]{6, 6, 6, 6, 6, 6}, new int[]{4, 3, 2, 1, 6, 4}, new int[]{0, 0, 0, 0, 25, 15}));
		Competitor competitorSecond = new Competitor(userSecond, mockResults(new int[]{6, 6, 6, 6, 6, 6}, new int[]{4, 3, 2, 1, 6, 4}, new int[]{0, 0, 0, 0, 25, 14})); 
		Competitor competitorThird = new Competitor(userThird, mockResults(new int[]{5, 6, 6, 6, 6, 6}, new int[]{4, 3, 2, 1, 6, 4}, new int[]{0, 0, 0, 0, 25, 15})); 
		Competitor competitorFourth = new Competitor(userFourth, mockResults(new int[]{5, 6, 6, 6, 6, 6}, new int[]{3, 3, 2, 1, 6, 4}, new int[]{0, 0, 0, 0, 25, 14}));
		List<Competitor> competitors = new ArrayList<Competitor>(4);
		competitors.add(competitorFourth);
		competitors.add(competitorThird);
		competitors.add(competitorSecond);
		competitors.add(competitorFirst);
		List<Competitor> sortedCompetitors = Results.sortResults(competitors);
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
		List<Result> results = new ArrayList<Result>();
		for (int i = 0; i < hits.length; i++) {
			results.add(new Result(hits[i], targets[i], points[i]));
		}
		return results;
	}
}
