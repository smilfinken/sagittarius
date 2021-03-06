package models;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Squad extends Model {
	public int number;
	public int slots;
	@OneToMany
	public List<Competitor> competitors;

	public Squad(int number){
		this.number = number;
		this.slots = -1;
	}

	public Squad(int number, int slots){
		this.number = number;
		this.slots = slots;
	}
}