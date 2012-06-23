package models;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Category extends Model {

	public String label;
	@ManyToMany(mappedBy = "categories")
	public List<Division> divisions;

	public Category(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}

	public Element toXML() {
		Element categoryElement = DocumentHelper.createElement(this.getClass().getSimpleName());
		categoryElement.addAttribute("label", label);
		return categoryElement;
	}
}