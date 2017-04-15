package models;

import javax.persistence.*;
import play.data.validation.*;

@Entity
public class TargetScore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(nullable = false)
    public Integer value;

    public TargetScore() {
        this.value = 0;
    }

    public TargetScore(Integer value) {
        this.value = value;
    }
}
