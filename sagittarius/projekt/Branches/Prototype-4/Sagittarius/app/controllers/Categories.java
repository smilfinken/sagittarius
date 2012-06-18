package controllers;

import models.Category;

/**
 *
 * @author johan
 */
@CRUD.For(Category.class)
public class Categories extends CRUD {

	@Override
	public String toString() {
		return "Kategorier";
	}
}