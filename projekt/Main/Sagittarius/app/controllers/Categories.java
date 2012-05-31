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

	public static void edit(long categoryID) {
	}
}