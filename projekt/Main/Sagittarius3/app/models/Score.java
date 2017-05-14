package models;

import java.util.List;
import java.util.ArrayList;

public class Score {
    public Long competitionId;
    public Long competitorId;
    public String competitorName;
    public String competitorClub;
    public String competitorClass;
    public String competitorCategory;
    public List<StageScore> scores;

    public Score(Long competitionId, Competitor competitor, boolean championship) {
        this.competitionId = competitionId;
        this.competitorId = competitor.id;
        this.competitorName = competitor.fullName();
        this.competitorClub = competitor.clubName();
        this.competitorClass = competitor.combinedClass(championship);
        switch (competitorClass) {
            case "C": case "C1": case "C2": case "C3": case "D": case "D1": case "D2": case "D3": case "VY": case "VÄ": case "J":
                this.competitorCategory = "C";
                break;
            case "B": case "B1": case "B2": case "B3":
                this.competitorCategory = "B";
                break;
            case "A": case "A1": case "A2": case "A3":
                this.competitorCategory = "A";
                break;
            case "R": case "R1": case "R2": case "R3":
                this.competitorCategory = "R";
                break;
            default:
                this.competitorCategory = "X";
        }
        this.scores = new ArrayList<StageScore>(competitor.scores);
    }

    public String scoreString() {
        int shots = 0;
        int targets = 0;
        for (StageScore score : scores) {
            shots += score.hits();
            targets += score.targets();
        }
        return String.format("%s/%s", shots, targets);
    }

    public int scoreTotal() {
        int shots = 0;
        int targets = 0;
        for (StageScore score : scores) {
            shots += score.hits();
            targets += score.targets();
        }
        return shots + targets;
    }

    public int points() {
        int points = 0;
        for (StageScore score : scores) {
            points += score.points;
        }
        return points;
    }
}
