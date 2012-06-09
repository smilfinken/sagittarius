package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Category extends Model {

	public String label;
	
	@ManyToMany(mappedBy="categories")
	public List<Division> divisions;

	public Category(String label) {
		this.label = label;
	}
}