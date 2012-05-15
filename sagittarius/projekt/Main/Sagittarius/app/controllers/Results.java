package controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import models.*;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Results extends Controller {

	private static void addResult(Competitor competitor, List<Result> results) {
		competitor.results = results;
		competitor.save();
	}

	private static int sumPoints(List<Result> results) {
		int points = 0;

		for (Result result : results) {
			points += result.points;
		}

		return points;
	}

	private static int sumResults(List<Result> results) {
		int hits = 0;
		int targets = 0;

		for (Result result : results) {
			hits += result.hits;
			targets += result.targets;
		}

		return hits + targets;
	}

	static List<Competitor> sortCompetitors(List<Competitor> competitors) {
		class CompetitorListComparator implements Comparator<Competitor> {

			@Override
			public int compare(Competitor A, Competitor B) {
				return A.getFullName().compareToIgnoreCase(B.getFullName());
			}
		}

		List<Competitor> out = competitors;
		CompetitorListComparator c = new CompetitorListComparator();

		Collections.sort(out, c);
		return out;
	}

	static List<Competitor> sortResults(List<Competitor> competitors) {
		final List<String> classOrder = Arrays.asList("J", "C", "D", "V", "B", "A", "R", "S");

		class ResultListComparator implements Comparator<Competitor> {

			@Override
			public int compare(Competitor A, Competitor B) {
				if (A.getDivision().equals(B.getDivision())) {
					if (sumResults(B.results) == sumResults(A.results)) {
						return sumPoints(B.results) - sumPoints(A.results);
					} else {
						return sumResults(B.results) - sumResults(A.results);
					}
				} else {
					String classA = A.getDivision().substring(0, Math.max(1, A.getDivision().length() - 1));
					String classB = B.getDivision().substring(0, Math.max(1, B.getDivision().length() - 1));
					int classSort = classOrder.indexOf(classA) - classOrder.indexOf(classB);

					if (classSort != 0) {
						return classSort;
					} else {
						String rankA = A.getDivision().substring(A.getDivision().length() - 1);
						String rankB = B.getDivision().substring(B.getDivision().length() - 1);

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

	private static void showResults(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Competitor> results = sortResults(competition.competitors);
		List<Competitor> competitors = new ArrayList<>();
		for (Competitor competitor : results) {
			if (!competitor.isScored()) {
				competitors.add(competitor);
			}
		}

		render(competition, results, sortCompetitors(competitors));
	}

	@SuppressWarnings("unused")
	private static void deleteEntry(long competitorID) {
		Competitor entry = Competitor.findById(competitorID);

		if (entry != null) {
			entry.delete();
		}
	}

	public static void list(int competitionID) {
		showResults(competitionID);
	}

	public static void add(long competitionID, long competitorID, List<Result> results) {
		Competitor competitor = Competitor.findById(competitorID);
		if (competitor != null) {
			addResult(competitor, results);
		}
		showResults(competitionID);
	}

	public static void delete(long competitionID, long competitorID) {
		Competitor competitor = Competitor.findById(competitorID);
		if (competitor != null) {
			competitor.results = new ArrayList<>();
			competitor.save();
		}
		showResults(competitionID);
	}

	public static void edit(long competitionID, long competitorID) {
		showResults(competitionID);
	}

	public static void enter() {
		render();
	}

	public static void export(long competitionID) throws IOException {
		Competition competition = Competition.findById(competitionID);
		if (competition != null) {
			List<Competitor> results = competition.competitors;

			File resultsFile = File.createTempFile("sgt", "csv");
			resultsFile.deleteOnExit();

			try {
				FileWriter resultsWriter = new FileWriter(resultsFile);
				String header1 = ";";
				String header2 = "Namn;Klass";
				for (int i = 1; i <= 6; i++) {
					header1 += String.format(";\"Station %d\";;", i);
					header2 += String.format(";\"Träff\";\"Figurer\";\"Poäng\"", i);
				}
				header1 += ";Summa;;;;\n";
				header2 += ";\"Träff\";\"Figurer\";\"Totalt\";\"Poäng\"\n";
				resultsWriter.write(header1 + header2);

				for (Competitor competitor : sortResults(results)) {
					String line = String.format("\"%s\";\"%s\"", competitor.getFullName(), competitor.getDivision());
					int totalHits = 0;
					int totalTargets = 0;
					int totalPoints = 0;

					for (Result result : competitor.results) {
						line += String.format(";%d;%d;%d", result.hits, result.targets, result.points);
						totalHits += result.hits;
						totalTargets += result.targets;
						totalPoints += result.points;
					}

					line += String.format(";%d;%d;%d;%d\n", totalHits, totalTargets, totalHits + totalTargets, totalPoints);
					resultsWriter.write(line);
				}
			} catch (IOException e) {
			}

			response.setHeader("Content-Length", String.format("%d", resultsFile.length()));
			response.setHeader("Content-Type", "text/csv; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=out.csv");
			renderBinary(resultsFile);
		}
	}

	public static void newUser(long competitionID) {
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		List<Division> divisions = Division.all().fetch();

		render(competitionID, categories, ranks, divisions);
	}

	public static void addUser(long competitionID, String firstName, String surname, long rankID, long categoryID, long divisionID) {
		Competition competition = Competition.findById(competitionID);
		@SuppressWarnings("unused")
		List<Competitor> results = sortResults(competition.competitors);
		@SuppressWarnings("unused")
		List<Competitor> competitors = sortCompetitors(competition.competitors);

		Rank rank = Rank.findById(rankID);
		Category category = Category.findById(categoryID);
		User user = new User(firstName, surname, rank, Arrays.asList(category));
		user.save();

		Division division = Division.findById(divisionID);
		Competitor competitor = new Competitor(user, division);
		competitor.willBeSaved = true;
		competition.competitors.add(competitor);
		competition.save();

		showResults(competitionID);
	}
}