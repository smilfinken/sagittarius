package models;

public class TeamScore implements Comparable {
    public Long competitorId;
    public String competitorName;
    public String competitorClub;
    public String competitorClass;
    public int shots;
    public int targets;
    public int points;

    public TeamScore(Competitor competitor) {
        this.competitorId = competitor.id;
        this.competitorName = competitor.fullName();
        this.competitorClub = competitor.clubName();
        this.competitorClass = competitor.combinedClass();
        this.shots = competitor.totalShotsScored();
        this.targets = competitor.totalTargetsScored();
        this.points = competitor.totalPointsScored();
    }

    @Override
    public int compareTo(Object o) {
        TeamScore teamScore = (TeamScore)o;
        int a = this.shots + this.targets;
        int b = teamScore.shots + teamScore.targets;

        if (b != a) {
            return b - a;
        } else {
            return teamScore.points - this.points;
        }
    }

    public String resultString() {
        return String.format("%d/%d %d (%d)", shots, targets, shots + targets, points);
    }
}
