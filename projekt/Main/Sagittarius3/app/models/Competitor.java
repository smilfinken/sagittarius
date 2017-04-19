package models;

import javax.persistence.*;
import play.data.validation.*;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

@Entity
public class Competitor implements Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne
    public User user;

    @ManyToOne
    public CompetitionClass competitionClass;

    @ManyToOne
    public CompetitionSubclass competitionSubclass;

    @OneToMany(cascade = CascadeType.ALL)
    public List<StageScore> scores;

    public Competitor() {
        this.scores = new ArrayList<StageScore>();
    }

    public Competitor(CompetitionClass competitionClass) {
        this.competitionClass = competitionClass;
        this.scores = new ArrayList<StageScore>();
    }

    public Competitor(CompetitionClass competitionClass, CompetitionSubclass competitionSubclass) {
        this.competitionClass = competitionClass;
        this.competitionSubclass = competitionSubclass;
        this.scores = new ArrayList<StageScore>();
    }

    @Override
    public int compareTo(Object o) {
        Competitor competitor = (Competitor)o;
        if (this.clubName() != competitor.clubName()) {
            return competitor.clubName().compareTo(this.clubName());
        } else if (this.fullName() != competitor.fullName()) {
                return competitor.fullName().compareTo(this.clubName());
        } else {
            return competitor.combinedClass().compareTo(this.combinedClass());
        }
    }

    public String fullName() {
        return user.fullName();
    }

    public String clubName() {
        return user.club;
    }

    public String combinedClass() {
        if (competitionClass != null) {
            if (competitionSubclass != null) {
                return (String.format("%s%s%d", competitionClass.label, competitionSubclass.label, user.ranking.ranking));
            } else {
                return (String.format("%s%d", competitionClass.label, user.ranking.ranking));
            }
        } else {
            return "";
        }
    }

    public Boolean scored() {
        for (StageScore stageScore: scores) {
            if (!stageScore.scored()) {
                return false;
            }
        }
        return !scores.isEmpty();
    }

    public StageScore getStageScore(Integer index) {
        for (StageScore stageScore : scores) {
            if (stageScore.index == index) {
                return stageScore;
            }
        }
        return new StageScore();
    }

    public Integer totalShotsScored() {
        Integer result = 0;

        for (StageScore score : scores) {
            result += score.hits();
        }

        return result;
    }

    public Integer totalTargetsScored() {
        Integer result = 0;

        for (StageScore score : scores) {
            result += score.targets();
        }

        return result;
    }

    public Integer totalPointsScored() {
        Integer result = 0;

        for (StageScore score : scores) {
            result += score.points;
        }

        return result;
    }

    public String scoreString() {
        String result = "";

        result = String.format("%d/%d %d (%d)", totalShotsScored(), totalTargetsScored(), totalShotsScored() + totalTargetsScored(), totalPointsScored());

        return result;
    }

    public ObjectNode toJson() {
        return Json.newObject()
            .put("id", this.id)
            .put("name", this.fullName())
            .put("class", this.combinedClass())
            .put("club", this.clubName());
    }
}
