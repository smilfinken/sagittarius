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
public class TargetShape extends Model {

	@Column(nullable = false, unique = true)
	public String label;
	public int classification;
	public boolean scoring;
	@ManyToMany
	public List<CompetitionType> competitionTypes;
	@ManyToMany
	public List<TargetColour> availableColours;

	public TargetShape(String label) {
		this.label = label;
		scoring = false;
	}

	public TargetShape(String label, int classification, boolean scoring, List<CompetitionType> competitionTypes, List<TargetColour> availableColours) {
		this.label = label;
		this.classification = classification;
		this.scoring = scoring;
		this.competitionTypes = competitionTypes;
		this.availableColours = availableColours;
	}

	@Override
	public String toString() {
		return label;
	}

	public boolean hasCompetitionType(long competitionTypeID) {
		CompetitionType competitionType = CompetitionType.findById(competitionTypeID);

		return competitionTypes.contains(competitionType);
	}
}