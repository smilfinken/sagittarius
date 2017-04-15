package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import play.data.validation.*;

@Entity
public class StageScore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(nullable = false)
    public Integer index;

    @OneToMany()
    public List<TargetScore> targetScores;

    @Column(nullable = false)
    public Integer points;

    public StageScore() {
        this.index = 0;
        this.points = 0;
        this.targetScores = new ArrayList<>();
    }

    public StageScore(Integer index) {
        this.index = index;
        this.points = 0;
        this.targetScores = new ArrayList<>();
    }

    public Boolean scored() {
        return !targetScores.isEmpty();
    }

    public Integer hits() {
        Integer hits = 0;
        for (TargetScore targetScore : targetScores) {
            hits += targetScore.value;
        }
        return hits;
    }

    public Integer targets() {
        Integer targets = 0;
        for (TargetScore targetScore : targetScores) {
            if (targetScore.value > 0) {
                targets++;
            }
        }
        return targets;
    }

    public String scoreString() {
        return String.format("%s/%s", hits(), targets());
    }

    public Integer combinedScore() {
        return hits() + targets();
    }
}
