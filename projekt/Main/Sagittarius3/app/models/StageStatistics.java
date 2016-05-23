package models;

import java.util.DoubleSummaryStatistics;

public class StageStatistics {

    public Double shotAverage;
    public Double targetAverage;

    public StageStatistics(Double shotAverage, Double targetAverage) {
        this.shotAverage = shotAverage;
        this.targetAverage = targetAverage;
    }

    public String averageString() {
        return String.format("%.2f/%.2f", shotAverage, targetAverage);
    }
}