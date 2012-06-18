package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import models.Category;
import models.Competition;
import models.CompetitionType;
import models.Competitor;
import models.Division;
import models.Rank;
import models.ScoringType;
import models.Stage;
import models.User;
import play.mvc.Controller;
import play.mvc.With;
import static common.Sorting.*;
import java.io.File;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Competitions extends Controller {

	public static void list() {
		List<Competition> competitions = Competition.all().fetch();
		render(competitions);
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
		renderTemplate("Competitions/edit.html", competition, competitionTypes, scoringTypes, sortStages(competition.stages));
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
				case "addstages":
					competition = Competition.findById(competitionID);
					if (competition != null) {
						List<Stage> newstages = new ArrayList<>();
						for (int i = 1; i <= stages; i++) {
							Stage stage = new Stage(1, String.format("Station %d", i), i);
							stage.save();
							newstages.add(stage);
						}
						competition.stages = newstages;
						competition.save();
					}

					Stages.edit(competitionID, competition.stages.get(0).id);
					break;
				case "save":
					competition = Competition.findById(competitionID);
					if (competition != null) {
						competition.label = label;
						competition.date = date;
						competition.save();
					}

					List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
					details(competitionID);
					break;
				case "delete":
					competition = Competition.findById(competitionID);
					if (competition != null) {
						competition.deleteCompetitors();
						competition.deleteStages();
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
		List<Competitor> competitors = competition.competitors;
		List<User> users = User.all().fetch();
		List<Category> categories = Category.all().fetch();
		List<Rank> ranks = Rank.all().fetch();
		List<Division> divisions = Division.all().fetch();

		render(competition, sortCompetitors(competitors), sortUsers(users), categories, ranks, divisions);
	}

	public static void enroll(long competitionID, long divisionID) {
		Competition competition = Competition.findById(competitionID);
		User user = User.find("byEmail", session.get("username")).first();
		//List<Division> divisions = Division.find("byCategories", user.categories).fetch();

		if (params._contains("divisionID")) {
			Division division = Division.findById(divisionID);
			Competitor competitor = new Competitor(user, division);
			competition.competitors.add(competitor);
			competition.save();
		}

		List<Division> divisions = new ArrayList<>();
		List<Division> allDivisions = Division.all().fetch();
		for (Category category : user.categories) {
			for (Division division : allDivisions) {
				if (division.categories.contains(category) && !divisions.contains(division) && !competition.isEnrolled(user.email, division.id)) {
					divisions.add(division);
				}
			}
		}

		render(competition, divisions);
	}

	public static void unroll(long competitionID, long divisionID) {
		Competition competition = Competition.findById(competitionID);
		User user = User.find("byEmail", session.get("username")).first();

		if (params._contains("divisionID")) {
			Competitor competitor = null;
			for (Competitor candidate : competition.competitors) {
				if (candidate.division.id == divisionID) {
					competitor = candidate;
				}
			}
			if (competitor != null) {
				competition.competitors.remove(competitor);
				competition.save();
				competitor.delete();
			}
		}

		List<Division> divisions = new ArrayList<>();
		List<Division> allDivisions = Division.all().fetch();
		for (Category category : user.categories) {
			for (Division division : allDivisions) {
				if (division.categories.contains(category) && !divisions.contains(division) && competition.isEnrolled(user.email, division.id)) {
					divisions.add(division);
				}
			}
		}
		renderTemplate("Competitions/enroll.html", competition, divisions);
	}

	@Check("admin")
	public static void addStage(long competitionID, String name) {
		Competition competition = Competition.findById(competitionID);

		if (competition != null) {
			Stage stage = new Stage(1, name, competition.stages.size() + 1);
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
			competition.deleteStage(stageID);
		}

		details(competitionID);
	}

	@Check("admin")
	public static void registerUser(long competitionID, long userID, long divisionID) {
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

	@Check("admin")
	public static void unregisterUser(long competitionID, long competitorID) {
		Competition competition = Competition.findById(competitionID);

		if (competition != null) {
			competition.deleteCompetitor(competitorID);
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
}