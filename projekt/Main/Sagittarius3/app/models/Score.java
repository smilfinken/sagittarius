package models;

import java.util.ArrayList;

public class Score {
    public Long competitionId;
    public Long competitorId;
    public String competitorName;
    public String competitorClub;
    public String competitorClass;
    public String competitorCategory;
    public ArrayList<StageScore> scores;
    public Boolean championship;
    public Integer stagecount;

    public Score(Long competitionId, Competitor competitor, boolean championship, Integer stagecount) {
        this.competitionId = competitionId;
        this.competitorId = competitor.id;
        this.competitorName = competitor.fullName();
        this.competitorClub = competitor.clubName();
        this.competitorClass = competitor.combinedClass(championship);
        switch (competitorClass) {
            case "C": case "C1": case "C2": case "C3": case "J": case "D": case "D1": case "D2": case "D3": case "VY": case "VÄ":
                this.competitorCategory = "C";
                break;
            case "CJ":
                this.competitorCategory = "J";
                break;
            case "CD":
                this.competitorCategory = "D";
                break;
            case "CVY":
                this.competitorCategory = "VY";
                break;
            case "CVÄ":
                this.competitorCategory = "VÄ";
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
        this.scores = new ArrayList<>(competitor.scores);
        this.championship = championship;
        this.stagecount = stagecount;
    }

    public String scoreString() {
        int shots = 0;
        int targets = 0;
        int count = 0;
        for (StageScore score : scores) {
            if (count++ < stagecount) {
                shots += score.hits();
                targets += score.targets();
            }
        }
        return String.format("%s/%s", shots, targets);
    }

    public int scoreTotal() {
        int shots = 0;
        int targets = 0;
        int count = 0;
        for (StageScore score : scores) {
            if (count++ < stagecount) {
                shots += score.hits();
                targets += score.targets();
            }
        }
        return shots + targets;
    }

    public int points() {
        int points = 0;
        int count = 0;
        for (StageScore score : scores) {
            if (count++ < stagecount) {
                points += score.points;
            }
        }
        return points;
    }

    public int tiebreaker() {
        int shots = 0;
        int targets = 0;
        int count = 0;
        for (StageScore score : scores) {
            if (count++ >= stagecount) {
                shots += score.hits();
                targets += score.targets();
            }
        }
        return shots + targets;
    }
}
