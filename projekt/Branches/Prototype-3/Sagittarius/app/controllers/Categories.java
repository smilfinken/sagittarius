package controllers;

import java.util.List;
import models.Category;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Categories extends Controller {

	public static void list() {
		List<Category> categories = Category.all().fetch();
		render(categories);
	}

	@Check("admin")
	public static void edit(long categoryID, String category, String useraction) {
				Category item = Category.findById(categoryID);

		if (params._contains("useraction")) {
			switch (useraction) {
				case "save":
					item.category = category;
					item.save();
					list();
					break;
				case "delete":
					item.delete();
					list();
					break;
			}

		}

		render(item);
	}
}