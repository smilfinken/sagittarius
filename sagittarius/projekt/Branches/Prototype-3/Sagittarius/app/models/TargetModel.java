package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class TargetModel extends Model {

	public String name;
	public String colour;
	public int modelGroup;
	public String supplier;
	public String supplierID;

	public TargetModel(String name, String colour) {
		this.name = name;
		this.colour = colour;
		this.modelGroup = -1;
		this.supplier = "";
		this.supplierID = "";
	}

	public String getModel(){
		return String.format("%s %s", colour, name);
	}
}