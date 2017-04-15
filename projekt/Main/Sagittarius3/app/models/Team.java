package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import play.data.validation.*;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(nullable = false)
    public String club;

    @Column(nullable = false)
    public String label;

    @ManyToMany
    public List<Competitor> competitors;

    @ManyToMany
    public List<CompetitionClass> allowedClasses;

    public Team() {
        this.club = "";
        this.label = "";
        this.competitors = new ArrayList<>();
        this.allowedClasses = new ArrayList<>();
    }

    public void copyValues(Team source) {
        this.club = source.club;
        this.label = source.label;
        this.allowedClasses = source.allowedClasses;
    }

    public boolean scored() {
        boolean result = competitors.size() > 0;

        for(Competitor competitor: competitors) {
            result = result && competitor.scored();
        }

        return result;
    }
    public String resultString() {
        String result = "";

        if (scored()) {
            Integer shots = 0;
            Integer targets = 0;
            Integer points = 0;

            for (Competitor competitor: competitors) {
                shots += competitor.totalShotsScored();
                targets += competitor.totalTargetsScored();
                points += competitor.totalPointsScored();
            }

            result = String.format("%d/%d %d (%d)", shots, targets, shots + targets, points);
        }

        return result;
    }
}
