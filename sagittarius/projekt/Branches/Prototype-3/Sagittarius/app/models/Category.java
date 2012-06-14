package models;

import javax.persistence.Entity;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
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

	public Element toXML() {
		Element categoryElement = DocumentHelper.createElement(this.getClass().getSimpleName());
		categoryElement.addAttribute("label", category);
		return categoryElement;
	}
}