package controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;
import models.*;
import play.mvc.Controller;
import play.mvc.With;
import static play.modules.pdf.PDF.*;

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

	private static int compareStages(Competitor A, Competitor B) {
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
						if (sumPoints(B.results) == sumPoints(A.results)) {
							return compareStages(A, B);
						} else {
							return sumPoints(B.results) - sumPoints(A.results);
						}
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

	public static void list(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Competitor> results = sortResults(competition.competitors);
		List<Competitor> competitors = new ArrayList<>();
		for (Competitor competitor : results) {
			if (!competitor.isScored()) {
				competitors.add(competitor);
			}
		}

		renderTemplate("Results/list.html", competition, results, sortCompetitors(competitors));
	}

	public static void add(long competitionID, long competitorID, List<Result> results) {
		Competitor competitor = Competitor.findById(competitorID);
		if (competitor != null) {
			addResult(competitor, results);
		}
		list(competitionID);
	}

	public static void delete(long competitionID, long competitorID) {
		Competitor competitor = Competitor.findById(competitorID);
		if (competitor != null) {
			competitor.results = new ArrayList<>();
			competitor.save();
		}
		list(competitionID);
	}

	public static void edit(long competitionID, long competitorID) {
		list(competitionID);
	}

	public static void export(long competitionID) throws IOException {
		Competition competition = Competition.findById(competitionID);
		if (competition != null) {
			File resultsFile = File.createTempFile("sgt", "csv");
			resultsFile.deleteOnExit();

			try (FileWriter resultsWriter = new FileWriter(resultsFile)) {
				String header1 = ";";
				String header2 = "Namn;Klass";
				for (int i = 1; i <= 6; i++) {
					header1 += String.format(";\"Station %d\";;", i);
					header2 += String.format(";\"Träff\";\"Figurer\";\"Poäng\"", i);
				}
				header1 += ";Summa;;;;\n";
				header2 += ";\"Träff\";\"Figurer\";\"Totalt\";\"Poäng\"\n";
				resultsWriter.write(header1 + header2);

				for (Competitor competitor : sortResults(competition.getScoredCompetitors())) {
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
				resultsWriter.close();
			} catch (IOException e) {
			}

			response.setHeader("Content-Length", String.format("%d", resultsFile.length()));
			response.setHeader("Content-Type", "text/csv; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=out.csv");
			renderBinary(resultsFile);
		}
	}

	//('NAMN_PÅ_TÄVLING',MAX_TRÄFF,'KLASS','SKYTTENS_NAMN',TRÄFF,'DATUM',PLACERING,0)
	public static void exportToShooter(long competitionID) throws IOException {
		Competition competition = Competition.findById(competitionID);
		if (competition != null) {
			List<Competitor> results = competition.competitors;

			File resultsFile = File.createTempFile("sgt", "csv");
			resultsFile.deleteOnExit();

			try (FileWriter resultsWriter = new FileWriter(resultsFile)) {
				int place = 1;
				String division = "";
				for (Competitor competitor : sortResults(results)) {
					if (!competitor.getDivision().equals(division)) {
						place = 1;
					} else {
						place++;
					}
					String line = String.format("'%s',%d,'%s','%s',%d,'%s',%d,%d\r\n", competition.name, competition.getMaxScore(), competitor.getDivision(), competitor.getFullName(), competitor.getScore(), competition.getDate(), place, 0);
					resultsWriter.write(line);
					division = competitor.getDivision();
				}
				resultsWriter.close();
			} catch (IOException e) {
			}

			response.setHeader("Content-Length", String.format("%d", resultsFile.length()));
			response.setHeader("Content-Type", "text/csv; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=out.csv");
			renderBinary(resultsFile);
		}
	}

	public static void generatePDF(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Competitor> results = sortResults(competition.competitors);
		List<Competitor> competitors = new ArrayList<>();
		for (Competitor competitor : results) {
			if (!competitor.isScored()) {
				competitors.add(competitor);
			}
		}

		Date currentDate = new Date();
		String timeStamp = String.format("%s %s", DateFormat.getDateInstance(DateFormat.LONG).format(currentDate), DateFormat.getTimeInstance(DateFormat.SHORT).format(currentDate));
		Options options = new Options();
		options.filename = String.format("%s - %s.pdf", competition.name, competition.getDate());
		//TODO: figure out why css classes do not work in the header and footer
		options.HEADER_TEMPLATE = "Results/header.html";
		options.FOOTER_TEMPLATE = "Results/footer.html";
		options.FOOTER = "public/stylesheets/results.css";
		renderPDF("Results/print.html", options, competition, results, sortCompetitors(competitors), timeStamp);
	}

	public static void registerUser(long competitionID, long userID) {
		Competition competition = Competition.findById(competitionID);
		List<User> users = User.all().fetch();
		List<Division> divisions = Division.all().fetch();

		renderTemplate("Competitors/registration.html", competition, userID, users, divisions);
	}

	public static void unregisterUser(long competitionID, long competitorID) {
		Competition competition = Competition.findById(competitionID);
		Competitor competitor = Competitor.findById(competitorID);

		// TODO: fix this so that data is properly removed from db on deletion
		if (competitor != null) {
			competition.competitors.remove(competitor);
			competition.save();
			competitor.results = null;
			competitor.delete();
		}

		List<Competitor> competitors = competition.competitors;
		List<User> users = User.all().fetch();

		list(competitionID);
	}

	public static void addUser(long competitionID, String firstName, String surname, long rankID, long categoryID, long divisionID) {
		Competition competition = Competition.findById(competitionID);

		if (firstName.length() > 0 && surname.length() > 0) {
			competition.willBeSaved = true;
			Rank rank = Rank.findById(rankID);
			Category category = Category.findById(categoryID);
			User user = new User(firstName, surname, rank, Arrays.asList(category));
			user.save();

			Division division = Division.findById(divisionID);
			Competitor competitor = new Competitor(user, division);
			competitor.willBeSaved = true;
			competition.competitors.add(competitor);
		}

		list(competitionID);
	}
}