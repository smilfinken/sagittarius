package models;

import play.Logger;

import java.util.ArrayList;
import java.util.List;

public class TeamResults implements Comparable{
    public Long competitionId;
    public Long teamId;
    public String teamLabel;
    public String teamClub;
    public String teamClasses;
    public List<TeamScore> scores;

    public TeamResults(Long competitionId, Team team) {
        this.competitionId = competitionId;
        this.teamId = team.id;
        this.teamLabel = team.label;
        this.teamClub = team.club;
        this.scores = new ArrayList<>();
        this.teamClasses = "";
        for (CompetitionClass competitionClass: team.allowedClasses) {
            this.teamClasses += this.teamClasses.length() > 0 ? ", " : "" + competitionClass.label;
        }
    }

    @Override
    public int compareTo(Object o) {
        TeamResults teamResults = (TeamResults)o;

        String teamClassesA = this.teamClasses;
        String teamClassesB = teamResults.teamClasses;
        if (!teamClassesA.equals(teamClassesB)) {
            return teamClassesB.compareTo(teamClassesA);
        }

        int shotsTotalA = this.shotsTotal() + this.targetsTotal();
        int shotsTotalB = teamResults.shotsTotal() + teamResults.targetsTotal();
        if (shotsTotalA != shotsTotalB) {
            return shotsTotalB - shotsTotalA;
        }

        return teamResults.targetsTotal() - this.targetsTotal();
    }

    public int shotsTotal() {
        int result = 0;

        for (TeamScore score: scores) {
            result += score.shots;
        }

        return result;
    }

    public int targetsTotal() {
        int result = 0;

        for (TeamScore score: scores) {
            result += score.targets;
        }

        return result;
    }

    public int pointsTotal() {
        int result = 0;

        for (TeamScore score: scores) {
            result += score.points;
        }

        return result;
    }

    public String resultString() {
        return String.format("%d/%d %d (%d)", shotsTotal(), targetsTotal(), shotsTotal() + targetsTotal(), pointsTotal());
    }
}
