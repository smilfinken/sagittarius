package controllers;

import java.util.ArrayList;
import java.util.List;
import models.Category;
import models.CompetitionType;
import models.Division;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Divisions extends Controller {

	public static void list() {
		List<Division> divisions = Division.all().fetch();
		render(divisions);
	}

	@Check("admin")
	public static void edit(long divisionID, String label, boolean ranks, long[] validCompetitionTypes, long[] validCategories, String useraction) {
		Division item = Division.findById(divisionID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					List<CompetitionType> newCTs = new ArrayList<>();
					for (long i : validCompetitionTypes) {
						CompetitionType ct = CompetitionType.findById(i);
						if (ct != null) {
							newCTs.add(ct);
						}
					}
					List<Category> newCs = new ArrayList<>();
					for (long i : validCategories) {
						Category c = Category.findById(i);
						if (c != null) {
							newCs.add(c);
						}
					}
					item.label = label;
					item.ranks = ranks;
					item.categories = newCs;
					item.competitionTypes = newCTs;
					item.save();
					list();
					break;
				case "delete":
					item.delete();
					list();
					break;
			}

		}

		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		List<Category> categories = Category.all().fetch();
		render(item, competitionTypes, categories);
	}
}