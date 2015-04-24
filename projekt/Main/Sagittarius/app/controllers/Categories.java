package controllers;

import models.Category;
import play.i18n.Messages;

/**
 *
 * @author johan
 */
@CRUD.For(Category.class)
public class Categories extends CRUD {

	@Override
	public String toString() {
		return Messages.get("divisions.categories");
	}
}