package models;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Competitor extends Model {

	@OneToOne
	public User user;
	@OneToOne
	public Division division;
	@OneToMany(cascade = CascadeType.ALL)
	public List<Result> results;

	public Competitor(User user) {
		this.user = user;
		this.results = null;
	}

	public Competitor(User user, Division division) {
		this.user = user;
		this.division = division;
		this.results = null;
	}

	public Competitor(User user, Division division, List<Result> results) {
		this.user = user;
		this.division = division;
		this.results = results;

		if (results != null) {
			for (Result result : results) {
				result.create();
			}
		}
	}

	public String getFullName() {
		return String.format("%s %s", user.firstName, user.surname);
	}

	public String getDivision() {
		if (division != null) {
			if (division.ranks) {
				return String.format("%s%s", division.division, user.rank.rank);
			} else {
				return String.format("%s", division.division);
			}
		} else {
			return "";
		}
	}

	public boolean isScored() {
		return (results.size() > 0);
	}
}