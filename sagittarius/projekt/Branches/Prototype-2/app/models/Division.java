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
public class Division extends Model {

	public String division;
	@ManyToMany
	public List<CompetitionType> competitionTypes;
	@ManyToMany
	public List<Category> categories;
	public boolean ranks;

	public Division(String division) {
		this.competitionTypes = null;
		this.division = division;
		this.categories = null;
		this.ranks = true;
	}

	public Division(List<CompetitionType> competitionTypes, String division) {
		this.competitionTypes = competitionTypes;
		this.division = division;
		this.categories = null;
		this.ranks = true;
	}

	public Division(List<CompetitionType> competitionTypes, String division, List<Category> categories) {
		this.competitionTypes = competitionTypes;
		this.division = division;
		this.categories = categories;
		this.ranks = true;
	}

	public Division(List<CompetitionType> competitionTypes, String division, List<Category> categories, boolean ranks) {
		this.competitionTypes = competitionTypes;
		this.division = division;
		this.categories = categories;
		this.ranks = ranks;
	}
}