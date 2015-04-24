package controllers;

import static common.Sorting.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.*;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Competitions extends Controller {

	public static void list() {
		List<Competition> competitions = Competition.all().fetch();
		List<Competition> entries = competitions;
		render(competitions, entries);
	}

	@Check("admin")
	public static void create() {
		List<CompetitionType> competitionTypes = CompetitionType.findAll();
		List<ScoringType> scoringTypes = ScoringType.findAll();
		render(competitionTypes, scoringTypes);
	}

	@Check("admin")
	public static void details(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		flash.put("competitionTypeID", competition.competitionType.id);
		List<ScoringType> scoringTypes = ScoringType.all().fetch();
		flash.put("scoringTypeID", competition.scoringType.id);
		render(competition, competitionTypes, scoringTypes);
	}

	@Check("admin")
	public static void edit(long competitionID, String label, Date date, long competitionTypeID, long scoringTypeID, int stages, String useraction) {
		Competition competition;

		if (params._contains("useraction")) {
			switch (useraction) {
				case "create":
					competition = new Competition(label);
					competition.date = date;
					competition.competitionType = CompetitionType.findById(competitionTypeID);
					competition.scoringType = ScoringType.findById(scoringTypeID);
					competition.save();

					if (competition.competitionType.hasStages) {
						stages(competition.id);
					} else {
						competitors(competition.id);
					}
					break;
				case "createadd":
					competition = new Competition(label);
					competition.date = date;
					competition.competitionType = CompetitionType.findById(competitionTypeID);
					competition.scoringType = ScoringType.findById(scoringTypeID);
					competition.save();

					create();
					break;
				case "finish":
					competition = new Competition(label);
					competition.date = date;
					competition.competitionType = CompetitionType.findById(competitionTypeID);
					competition.scoringType = ScoringType.findById(scoringTypeID);
					competition.save();

					schedule();
					break;
				case "addstages":
					competition = Competition.findById(competitionID);
					if (competition != null) {
						List<Stage> newstages = new ArrayList<>();
						for (int i = 1; i <= stages; i++) {
							Stage stage = new Stage(1, String.format("%d", i), i);
							stage.save();
							newstages.add(stage);
						}
						competition.stages = newstages;
						competition.save();
					}

					Stages.details(competitionID, competition.stages.get(0).id);
					break;
				case "save":
					competition = Competition.findById(competitionID);
					if (competition != null) {
						competition.label = label;
						competition.date = date;
						competition.save();
					}

					details(competitionID);
					break;
				case "delete":
					competition = Competition.findById(competitionID);
					if (competition != null) {
						competition.deleteSquads();
						competition.deleteStages();
						competition.clearCompetitors();
						competition.delete();
					}

					list();
					break;
			}

		}

		details(competitionID);
	}

	public static void stages(long competitionID) {
		Competition competition = Competition.findById(competitionID);

		render(competition);
	}

	public static void competitors(long competitionID) {
		Competition competition = Competition.findById(competitionID);
		List<Competitor> competitors = sortCompetitors(competition.competitors);
		List<User> users = User.all().fetch();
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		List<Division> divisions = Division.all().fetch();

		render(competition, competitors, sortUsers(users), categories, ranks, divisions);
	}

	public static void enrollment(long competitionID) {
		Competition competition = Competition.findById(competitionID);

		User user = User.find("byEmail", session.get("username")).first();
		List<Division> availableDivisions = competition.getAvailableEntries(user);
		List<Division> enrolledDivisions = competition.getEnrolledEntries(user);
		render(competition, availableDivisions, enrolledDivisions);
	}

	public static void enroll(long competitionID, long divisionID, String useraction) {
		Competition competition = Competition.findById(competitionID);
		User user = User.find("byEmail", session.get("username")).first();
		Division division = Division.findById(divisionID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "enroll":
					if (user != null && division != null) {
						Competitor competitor = new Competitor(user, division);
						competition.enrollCompetitor(competitor);
					}
					break;
				case "unroll":
					if (user != null && division != null) {
						competition.unrollUser(user, division);
					}
					break;
			}
		}

		enrollment(competitionID);
	}

	@Check("admin")
	public static void addStage(long competitionID, String label) {
		Competition competition = Competition.findById(competitionID);

		if (competition != null) {
			Stage stage = new Stage(1, label, competition.stages.size() + 1);
			stage.save();
			competition.stages.add(stage);
			competition.save();
		}

		details(competitionID);
	}

	@Check("admin")
	public static void deleteStage(long competitionID, long stageID) {
		Competition competition = Competition.findById(competitionID);

		if (competition != null) {
			competition.deleteStage((Stage) Stage.findById(stageID)).delete();
		}

		details(competitionID);
	}

	@Check("admin")
	public static void addSquad(long competitionID, String label) {
		Competition competition = Competition.findById(competitionID);

		if (competition != null) {
			Squad squad = new Squad(label, competition.squads.size() + 1);
			squad.save();
			competition.squads.add(squad);
			competition.save();
		}

		details(competitionID);
	}

	@Check("admin")
	public static void deleteSquad(long competitionID, long squadID) {
		Competition competition = Competition.findById(competitionID);

		if (competition != null) {
			competition.deleteSquad((Squad) Squad.findById(squadID)).delete();
		}

		details(competitionID);
	}

	@Check("admin")
	public static void enrollUser(long competitionID, long userID, long divisionID) {
		Competition competition = Competition.findById(competitionID);
		User user = User.findById(userID);
		Division division = Division.findById(divisionID);

		if (user != null && division != null) {
			// TODO: Validate that given user can have given division!
			Competitor competitor = new Competitor(user, division);
			competitor.save();
			competition.competitors.add(competitor);
			competition.save();
		}

		competitors(competitionID);
	}

	public static void billboard(long competitionId) {
		render(competitionId);
	}

	public static void billboardpart(long competitionId) {
		Competition competition = Competition.findById(competitionId);
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
		render(competition, sortResults(results), sortCompetitors(competitors));
	}

	public static void userdivisions(long userId) {
		User user = User.findById(userId);
		if (user != null) {
			Set<Division> divisions = user.getValidDivisions();
			renderTemplate("Common/selectDivision.html", divisions);
		}
	}

	public static void schedule() {
		List<Competition> competitions = null;
		Integer year = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
		try {
			Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(String.format("%d-01-01", year));
			Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(String.format("%d-01-01", year + 1));
			competitions = Competition.find("date >= :startDate and date < :endDate").setParameter("startDate", startDate).setParameter("endDate", endDate).fetch();
		} catch (ParseException e) {
			competitions = Competition.all().fetch();
		}
		List<User> users = User.all().fetch();
		render("Competitions/schedule.html", sortCompetitions(competitions), sortUsers(users), year);
	}

	@Check("admin")
	public static void setOrganiser(long competitionID, long userID) {
		Competition competition = Competition.findById(competitionID);
		if (competition != null) {
			User staff = User.findById(userID);
			competition.setOrganiser(staff);
		}

		schedule();
	}

	@Check("admin")
	public static void removeOrganiser(long competitionID, long userID) {
		Competition competition = Competition.findById(competitionID);
		if (competition != null) {
			User staff = User.findById(userID);
			competition.removeOrganiser(staff);
		}

		schedule();
	}

	@Check("admin")
	public static void addStaff(long competitionID, long userID) {
		Competition competition = Competition.findById(competitionID);
		if (competition != null) {
			User staff = User.findById(userID);
			competition.addStaff(staff);
		}

		schedule();
	}

	@Check("admin")
	public static void removeStaff(long competitionID, long userID) {
		Competition competition = Competition.findById(competitionID);
		if (competition != null) {
			User staff = User.findById(userID);
			competition.removeStaff(staff);
		}

		schedule();
	}

	private static Document parseXML(File file) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		return document;
	}

	public static void importCompetition(File file, String useraction) {
		if (params._contains("useraction")) {
			switch (useraction) {
				case "upload":
					if (file != null) {
						try {
							Competition competition = new Competition(parseXML(file).selectSingleNode("//Competition"));
							list();
						} catch (DocumentException | ParseException ex) {
							Logger.getLogger(Competitions.class.getName()).log(Level.SEVERE, "Failed to import XML from file", ex);
						}
						break;
					}
			}
		}

		render();
	}

	public static void listAsXML() {
		Document document = DocumentHelper.createDocument();
		List<Competition> competitions = Competition.all().fetch();
		Element root = document.addElement("data").addElement(Competitions.class.getSimpleName().toLowerCase());
		for (Competition competition : competitions) {
			Element element = root.addElement(competition.getClass().getSimpleName().toLowerCase());
			element.addAttribute("id", String.format("%d", competition.id));
			element.addAttribute("label", competition.label);
		}

		try {
			File exportFile = File.createTempFile("sgt", "xml");
			exportFile.deleteOnExit();

			FileWriter exportWriter = new FileWriter(exportFile);
			document.write(exportWriter);
			exportWriter.write('\n');
			exportWriter.close();

			response.setHeader("Content-Length", String.format("%d", exportFile.length()));
			response.setHeader("Content-Type", "text/xml; charset=utf-8");
			renderBinary(exportFile);
		} catch (IOException ex) {
			Logger.getLogger(Competitions.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
