package controllers;

import java.util.ArrayList;
import java.util.List;
import models.*;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Administration extends Controller {

	@Check("admin")
	public static void index() {
		render();
	}

	@Check("admin")
	public static void list_category() {
		List<Category> list = Category.all().fetch();
		render(list);
	}

	@Check("admin")
	public static void edit_category(long ID, String label, String useraction) {
		Category item = Category.findById(ID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					item.label = label;
					item.save();
					list_category();
					break;
				case "delete":
					item.delete();
					list_category();
					break;
			}

		}

		render(item);
	}

	@Check("admin")
	public static void list_competitiontype() {
		List<CompetitionType> list = CompetitionType.all().fetch();
		render(list);
	}

	@Check("admin")
	public static void edit_competitiontype(long ID, String label, String useraction) {
		CompetitionType item = CompetitionType.findById(ID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					item.label = label;
					item.save();
					list_competitiontype();
					break;
				case "delete":
					item.delete();
					list_competitiontype();
					break;
			}

		}

		render(item);
	}

	@Check("admin")
	public static void list_division() {
		List<Division> list = Division.all().fetch();
		render(list);
	}

	@Check("admin")
	public static void edit_division(long ID, String label, boolean ranks, long[] validCompetitionTypes, long[] validCategories, String useraction) {
		Division item = Division.findById(ID);

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
					list_division();
					break;
				case "delete":
					item.delete();
					list_division();
					break;
			}

		}

		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		List<Category> categories = Category.all().fetch();
		render(item, competitionTypes, categories);
	}

	@Check("admin")
	public static void list_rank() {
		List<Rank> list = Rank.all().fetch();
		render(list);
	}

	@Check("admin")
	public static void edit_rank(long ID, int ranking, String label, String useraction) {
		Rank item = Rank.findById(ID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					item.ranking = ranking;
					item.label = label;
					item.save();
					list_rank();
					break;
				case "delete":
					item.delete();
					list_rank();
					break;
			}

		}

		render(item);
	}

	@Check("admin")
	public static void list_scoringtype() {
		List<ScoringType> list = ScoringType.all().fetch();
		render(list);
	}

	@Check("admin")
	public static void edit_scoringtype(long ID, String label, boolean sumPointsAndTargets, boolean sumPointsOnly, String useraction) {
		ScoringType item = ScoringType.findById(ID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					item.label = label;
					item.sumPointsAndTargets = sumPointsAndTargets;
					item.sumPointsOnly = sumPointsOnly;
					item.save();
					list_scoringtype();
					break;
				case "delete":
					item.delete();
					list_scoringtype();
					break;
			}

		}

		render(item);
	}

	@Check("admin")
	public static void list_targetcolour() {
		List<TargetColour> list = TargetColour.all().fetch();
		render(list);
	}

	@Check("admin")
	public static void edit_targetcolour(long ID, String label, String useraction) {
		TargetColour item = TargetColour.findById(ID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					item.label = label;
					item.save();
					list_targetcolour();
					break;
				case "delete":
					item.delete();
					list_targetcolour();
					break;
			}

		}

		render(item);
	}

	@Check("admin")
	public static void list_targetclass() {
		List<TargetClass> list = TargetClass.all().fetch();
		render(list);
	}

	@Check("admin")
	public static void edit_targetclass(long ID, String classification, int maximumRange, String useraction) {
		TargetClass item = TargetClass.findById(ID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					item.classification = classification;
					item.maximumRange = maximumRange;
					item.save();
					list_targetclass();
					break;
				case "delete":
					item.delete();
					list_targetclass();
					break;
			}

		}

		flash.put("weaponCategoryID", item.weaponCategory.id);
		List<WeaponCategory> weaponCategories = WeaponCategory.all().fetch();
		render(item, weaponCategories);
	}

	@Check("admin")
	public static void list_targetshape() {
		List<TargetShape> list = TargetShape.all().fetch();
		render(list);
	}

	@Check("admin")
	public static void edit_targetshape(long ID, String label, int classification, long[] validCompetitionTypes, long[] validColours, String useraction) {
		TargetShape item = TargetShape.findById(ID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					item.label = label;
					item.save();
					list_targetshape();
					break;
				case "delete":
					item.delete();
					list_targetshape();
					break;
			}

		}

		List<TargetClass> targetClasses = TargetClass.all().fetch();
		List<CompetitionType> competitionTypes = CompetitionType.all().fetch();
		List<TargetColour> targetColours = TargetColour.all().fetch();
		render(item, targetClasses, competitionTypes, targetColours);
	}

	@Check("admin")
	public static void list_weaponcategory() {
		List<WeaponCategory> list = WeaponCategory.all().fetch();
		render(list);
	}

	@Check("admin")
	public static void edit_weaponcategory(long ID, String label, String shortLabel, double shotInterval, String useraction) {
		WeaponCategory item = WeaponCategory.findById(ID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					item.label = label;
					item.shortLabel = shortLabel;
					item.shotInterval = shotInterval;
					item.save();
					list_weaponcategory();
					break;
				case "delete":
					item.delete();
					list_weaponcategory();
					break;
			}

		}

		render(item);
	}
}