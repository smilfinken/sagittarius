package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Category extends Model {

	public String category;

	public Category(String category) {
		this.category = category;
	}
}