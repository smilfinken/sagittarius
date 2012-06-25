package models;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Division extends Model {

	@Column(nullable = false, unique = true)
	public String label;
	@ManyToMany
	public List<CompetitionType> competitionTypes;
	@ManyToMany
	public List<Category> categories;
	public boolean ranks;

	public Division(String label) {
		this.competitionTypes = null;
		this.label = label;
		this.categories = null;
		this.ranks = true;
	}

	public Division(List<CompetitionType> competitionTypes, String label) {
		this.competitionTypes = competitionTypes;
		this.label = label;
		this.categories = null;
		this.ranks = true;
	}

	public Division(List<CompetitionType> competitionTypes, String label, List<Category> categories) {
		this.competitionTypes = competitionTypes;
		this.label = label;
		this.categories = categories;
		this.ranks = true;
	}

	public Division(List<CompetitionType> competitionTypes, String label, List<Category> categories, boolean ranks) {
		this.competitionTypes = competitionTypes;
		this.label = label;
		this.categories = categories;
		this.ranks = ranks;
	}

	@Override
	public String toString() {
		return this.label;
	}

	public boolean hasCompetitionType(long competitionTypeID) {
		CompetitionType competitionType = CompetitionType.findById(competitionTypeID);

		return competitionTypes.contains(competitionType);
	}

	public boolean hasCategory(long categoryID) {
		Category category = Category.findById(categoryID);

		return categories.contains(category);
	}
}