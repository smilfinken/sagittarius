package common;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import models.Competitor;
import models.Result;
import models.User;

/**
 *
 * @author johan
 */
public class Sorting {

	public static List<User> sortUsers(List<User> users) {
		class UserComparator implements Comparator<User> {

			@Override
			public int compare(User A, User B) {
				if (A.surname.equals(B.surname)) {
					return (A.firstName.compareTo(B.firstName));
				} else {
					return (A.surname.compareTo(B.surname));
				}
			}
		}

		List<User> out = users;
		UserComparator c = new UserComparator();

		Collections.sort(out, c);
		return out;
	}

	public static List<Competitor> sortCompetitors(List<Competitor> competitors) {
		final List<String> classOrder = Arrays.asList("J", "C", "D", "V", "B", "A", "R", "S");

		class CompetitorComparator implements Comparator<Competitor> {

			@Override
			public int compare(Competitor A, Competitor B) {
				if (A.user.surname.equals(B.user.surname)) {
					if (A.user.firstName.equals(B.user.firstName)) {
						String classA = A.getDivisionAsString().substring(0, Math.max(1, A.getDivisionAsString().length() - 1));
						String classB = B.getDivisionAsString().substring(0, Math.max(1, B.getDivisionAsString().length() - 1));
						int classSort = classOrder.indexOf(classA) - classOrder.indexOf(classB);

						if (classSort != 0) {
							return classSort;
						} else {
							String rankA = A.getDivisionAsString().substring(A.getDivisionAsString().length() - 1);
							String rankB = B.getDivisionAsString().substring(B.getDivisionAsString().length() - 1);

							return rankA.compareTo(rankB);
						}
					} else {
						return A.user.firstName.compareTo(B.user.firstName);
					}
				} else {
					return A.user.surname.compareTo(B.user.surname);
				}
			}
		}

		List<Competitor> out = competitors;
		CompetitorComparator c = new CompetitorComparator();

		Collections.sort(out, c);
		return out;
	}

	static int sumPoints(List<Result> results) {
		int points = 0;

		for (Result result : results) {
			points += result.points;
		}

		return points;
	}

	static int sumResults(List<Result> results) {
		int hits = 0;
		int targets = 0;

		for (Result result : results) {
			hits += result.hits;
			targets += result.targets;
		}

		return hits + targets;
	}

	static int compareStages(Competitor A, Competitor B) {
		if (A != null && B != null) {
			if (A.results != null && B.results != null && A.results.size() == B.results.size()) {
				for (int i = A.results.size() - 1; i >= 0; i--) {
					Result a = A.results.get(i);
					Result b = B.results.get(i);

					if (a.hits + a.targets != b.hits + a.targets) {
						return (b.hits + b.targets) - (a.hits + a.targets);
					}
				}
			}
		}

		return 0;
	}

	public static List<Competitor> sortResults(List<Competitor> competitors) {

		final List<String> classOrder = Arrays.asList("J", "C", "D", "V", "B", "A", "R", "S");

		class ResultListComparator implements Comparator<Competitor> {

			@Override
			public int compare(Competitor A, Competitor B) {
				if (A.getDivisionAsString().equals(B.getDivisionAsString())) {
					if (sumResults(B.results) == sumResults(A.results)) {
						if (sumPoints(B.results) == sumPoints(A.results)) {
							return compareStages(A, B);
						} else {
							return sumPoints(B.results) - sumPoints(A.results);
						}
					} else {
						return sumResults(B.results) - sumResults(A.results);
					}
				} else {
					String classA = A.getDivisionAsString().substring(0, Math.max(1, A.getDivisionAsString().length() - 1));
					String classB = B.getDivisionAsString().substring(0, Math.max(1, B.getDivisionAsString().length() - 1));
					int classSort = classOrder.indexOf(classA) - classOrder.indexOf(classB);

					if (classSort != 0) {
						return classSort;
					} else {
						String rankA = A.getDivisionAsString().substring(A.getDivisionAsString().length() - 1);
						String rankB = B.getDivisionAsString().substring(B.getDivisionAsString().length() - 1);

						return rankA.compareTo(rankB);
					}
				}
			}
		}

		List<Competitor> out = competitors;
		ResultListComparator c = new ResultListComparator();

		Collections.sort(out, c);
		return out;
	}
}