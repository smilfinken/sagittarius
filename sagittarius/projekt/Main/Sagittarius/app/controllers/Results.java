package controllers;

import com.lowagie.text.Element;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;
import models.*;
import play.i18n.Messages;
import play.modules.pdf.PDF.Options;
import static play.modules.pdf.PDF.renderPDF;
import static common.Sorting.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Results extends Controller {

	public static void list(long competitionID) {
		Competition competition = Competition.findById(competitionID);

		List<Competitor> competitors = competition.getUnScoredCompetitors();
		List<Competitor> results = competition.getScoredCompetitors();
		render(competition, sortResults(results), sortCompetitors(competitors));
	}

	public static void add(long competitionID, long competitorID, List<Result> results) {
		Competitor competitor = Competitor.findById(competitorID);

		if (competitor != null) {
			competitor.addResults(results);
		}

		list(competitionID);
	}

	public static void addSquad(long competitionID, long squadID, Result[][] results, long[] competitors) {
		Squad squad = Squad.findById(squadID);
		if (squad != null) {
			for (int i = 0; i < results.length; i++) {
				Result[] resultlist = results[i];
				Competitor competitor = Competitor.findById(competitors[i]);
				if (competitor != null && squad.competitors.contains(competitor) && (competitor.results == null || competitor.results.size() == 0)) {
					competitor.addResults(Arrays.asList(resultlist));
				}
			}
		}

		list(competitionID);
	}

	public static void edit(long competitionID, long competitorID, List<Result> newResults, String useraction) {
		Competition competition = Competition.findById(competitionID);
		Competitor competitor = Competitor.findById(competitorID);

		if (competitor != null) {
			if (params._contains("useraction")) {
				switch (useraction) {
					case "save":
						competitor.addResults(newResults);
						list(competitionID);
						break;
					case "delete":
						competitor.deleteResults();
						list(competitionID);
						break;
				}
			}
		}

		render(competition, competitor);
	}

	public static void squad(long competitionID, long squadID) {
		Competition competition = Competition.findById(competitionID);
		Squad squad = Squad.findById(squadID);

		if (squad != null && squad.competitors != null && squad.competitors.size() != 0) {
			render(competition, squad);
		} else {
			list(competitionID);
		}
	}

	public static void export(long competitionID) throws IOException {
		Competition competition = Competition.findById(competitionID);
		if (competition != null) {
			File resultsFile = File.createTempFile("sgt", "csv");
			resultsFile.deleteOnExit();

			try (FileWriter resultsWriter = new FileWriter(resultsFile)) {
				String header1 = ";";
				String header2 = String.format("%s;%s", Messages.get("export.heading.competitorname"), Messages.get("export.heading.competitordivision"));
				for (int i = 1; i <= 6; i++) {
					header1 += String.format(";\"%s %d\";;", Messages.get("export.heading.stage"), i);
					header2 += String.format(";\"%s\";\"%s\";\"%s\"", Messages.get("export.heading.hits"), Messages.get("export.heading.targets"), Messages.get("export.heading.points"));
				}
				header1 += String.format(";%s;;;;\n", Messages.get("export.heading.sum"));
				header2 += String.format(";\"%s\";\"%s\";\"%s\";\"%s\"\n", Messages.get("export.heading.hits"), Messages.get("export.heading.targets"), Messages.get("export.heading.total"), Messages.get("export.heading.points"));
				resultsWriter.write(header1 + header2);

				for (Competitor competitor : sortResults(competition.getScoredCompetitors())) {
					String line = String.format("\"%s\";\"%s\"", competitor, competitor.getDivisionAsString());
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
			response.setHeader("Content-Disposition", String.format("attachment; filename=%s - %s.csv", competition, competition.getDate()));
			renderBinary(resultsFile);
		}
	}

	public static void exportXML(long competitionID) throws IOException {
		Competition competition = Competition.findById(competitionID);
		if (competition != null) {
			Document document = DocumentHelper.createDocument();
			document.add(competition.toXML());

			File exportFile = File.createTempFile("sgt", "xml");
			exportFile.deleteOnExit();

			try (FileWriter exportWriter = new FileWriter(exportFile)) {
				document.write(exportWriter);
				exportWriter.write('\n');
				exportWriter.close();
			} catch (IOException e) {
			}

			response.setHeader("Content-Length", String.format("%d", exportFile.length()));
			response.setHeader("Content-Type", "text/xml; charset=utf-8");
			response.setHeader("Content-Disposition", String.format("attachment; filename=%s - %s.xml", competition, competition.getDate()));
			renderBinary(exportFile);
		}
	}

	//('NAMN_PÅ_TÄVLING',MAX_TRÄFF,'KLASS','SKYTTENS_NAMN',TRÄFF,'DATUM',PLACERING,0)
	public static void exportToShooter(long competitionID) throws IOException {
		Competition competition = Competition.findById(competitionID);
		if (competition != null) {
			List<Competitor> results = sortResults(competition.getScoredCompetitors());

			File resultsFile = File.createTempFile("sgt", "csv");
			resultsFile.deleteOnExit();

			try (FileWriter resultsWriter = new FileWriter(resultsFile)) {
				int place = 1;
				String division = "";
				for (Competitor competitor : results) {
					if (!competitor.getDivisionAsString().equals(division)) {
						place = 1;
					} else {
						place++;
					}
					String line = String.format("'%s',%d,'%s','%s',%d,'%s',%d,%d\r\n", competition.label, competition.getMaxScore(), competitor.getDivisionAsString(), competitor, competitor.getScore(), competition.getDate(), place, 0);
					resultsWriter.write(line);
					division = competitor.getDivisionAsString();
				}
				resultsWriter.close();
			} catch (IOException e) {
			}

			response.setHeader("Content-Length", String.format("%d", resultsFile.length()));
			response.setHeader("Content-Type", "text/csv; charset=utf-8");
			response.setHeader("Content-Disposition", String.format("attachment; filename=%s - %s.csv", competition, competition.getDate()));
			renderBinary(resultsFile);
		}
	}

	public static void generatePDF(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Competitor> results = sortResults(competition.getScoredCompetitors());

		Date currentDate = new Date();
		String timeStamp = String.format("%s %s", DateFormat.getDateInstance(DateFormat.LONG).format(currentDate), DateFormat.getTimeInstance(DateFormat.SHORT).format(currentDate));
		Options options = new Options();
		options.filename = String.format("%s - %s.pdf", competition, competition.getDate());
		//TODO: figure out why css classes do not work in the header and footer
		options.HEADER_TEMPLATE = "Results/header.html";
		options.FOOTER_TEMPLATE = "Results/footer.html";
		options.FOOTER = "public/stylesheets/results.css";
		renderPDF("Results/print.html", options, competition, results, timeStamp);
	}

	public static void uploadXML(String xmlData) {
		try {
			Document document = DocumentHelper.parseText(xmlData);
			for (Iterator competitorNodeIterator = document.selectNodes("//competitors/competitor").iterator(); competitorNodeIterator.hasNext();) {
				Node competitorNode = (Node) competitorNodeIterator.next();
				try {
					Competitor competitor = Competitor.findById(Long.parseLong(competitorNode.valueOf("@id")));
					if (competitor != null) {
						ArrayList<Result> results = new ArrayList<>();
						for (Iterator scoreNodeIterator = competitorNode.selectNodes("scores/score").iterator(); scoreNodeIterator.hasNext();) {
							Node scoreNode = (Node) scoreNodeIterator.next();
							results.add(new Result(Integer.parseInt(scoreNode.valueOf("@hits")), Integer.parseInt(scoreNode.valueOf("@targets")), Integer.parseInt(scoreNode.valueOf("@points")), results.size() + 1));
						}
						competitor.addResults(results);
					}
				} catch (NumberFormatException ex) {
					Logger.getLogger(Results.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		} catch (DocumentException ex) {
			Logger.getLogger(Results.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}