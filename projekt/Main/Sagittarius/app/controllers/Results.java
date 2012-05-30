package controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;
import models.*;
import play.modules.pdf.PDF.Options;
import static play.modules.pdf.PDF.renderPDF;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Results extends Controller {

	static void addResult(Competitor competitor, List<Result> results) {
		competitor.results = results;
		competitor.save();
	}

	public static void list(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Competitor> allcompetitors = competition.competitors;

		List<Competitor> competitors = new ArrayList<>();
		List<Competitor> results = new ArrayList<>();
		for (Competitor competitor : allcompetitors) {
			if (!competitor.isScored()) {
				competitors.add(competitor);
			} else {
				results.add(competitor);
			}
		}

		renderTemplate("Results/list.html", competition, common.Sorting.sortResults(results), common.Sorting.sortCompetitors(competitors));
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

				for (Competitor competitor : common.Sorting.sortResults(competition.getScoredCompetitors())) {
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
				for (Competitor competitor : common.Sorting.sortResults(results)) {
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
		List<Competitor> results = common.Sorting.sortResults(competition.competitors);
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
		renderPDF("Results/print.html", options, competition, results, common.Sorting.sortCompetitors(competitors), timeStamp);
	}

	public static void unregisterUser(long competitionID, long competitorID) {
		Competition competition = Competition.findById(competitionID);
		Competitor competitor = Competitor.findById(competitorID);

		// TODO: fix this so that data is properly removed from db on deletion
		if (competition != null && competitor != null) {
			competition.competitors.remove(competitor);
			competition.save();
			competitor.results = null;
			competitor.delete();
		}

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