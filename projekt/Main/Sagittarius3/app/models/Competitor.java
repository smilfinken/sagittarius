package models;

import javax.persistence.*;
import play.data.validation.*;

import java.util.List;
import java.util.ArrayList;

@Entity
public class Competitor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne
    public User user;

    @Column(nullable = false)
    public String competitionClass;

    @Column(nullable = true)
    public String competitionSubclass;

    @Column(nullable = false)
    public boolean scored;

    @OneToMany
    public List<StageScore> scores;

    public Competitor() {
        this.competitionClass = "";
        this.competitionSubclass = "";
        this.scored = false;
        this.scores = new ArrayList<StageScore>();
    }

    public String fullName() {
        return user.fullName();
    }

    public String clubName() {
        return user.club;
    }

    public String combinedClass() {
        switch (competitionClass) {
            case "VY":
            case "VÄ":
                return competitionClass;
            default:
                return competitionClass + user.ranking;
        }
    }
}
