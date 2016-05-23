package models;

import javax.persistence.*;
import play.data.validation.*;

@Entity
public class StageScore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(nullable = false)
    public int index;

    @Column(nullable = false)
    public int shots;

    @Column(nullable = false)
    public int targets;

    @Column(nullable = false)
    public int points;

    public StageScore() {
        this.index = 0;
        this.targets = 0;
        this.shots = 0;
        this.points = 0;
    }

    public String scoreString() {
        return String.format("%s/%s", shots, targets);
    }

    public int combinedScore() {
        return this.targets + this.shots;
    }
}
