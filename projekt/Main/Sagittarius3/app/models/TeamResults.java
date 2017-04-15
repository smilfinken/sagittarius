package models;

import java.util.ArrayList;
import java.util.List;

public class TeamResults implements Comparable{
    public Long competitionId;
    public Long teamId;
    public String teamLabel;
    public String teamClub;
    public List<TeamScore> scores;

    public TeamResults(Long competitionId, Team team) {
        this.competitionId = competitionId;
        this.teamId = team.id;
        this.teamLabel = team.label;
        this.teamClub = team.club;
        this.scores = new ArrayList<>();
    }

    @Override
    public int compareTo(Object o) {
        TeamResults teamResults = (TeamResults)o;
        int a = this.shotsTotal() + this.targetsTotal();
        int b = teamResults.shotsTotal() + teamResults.targetsTotal();
        if (a != b) {
            return b - a;
        } else {
            return teamResults.pointsTotal() - this.pointsTotal();
        }
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
