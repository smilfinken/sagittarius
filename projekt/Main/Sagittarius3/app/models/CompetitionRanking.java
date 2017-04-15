package models;

import javax.persistence.*;
import play.data.validation.*;

@Entity
public class CompetitionRanking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(nullable = false)
    public int ranking;

    @Column(nullable = false)
    public String description;

    public CompetitionRanking() {
        this.ranking = 0;
        this.description = "";
    }

    public CompetitionRanking(int ranking, String description) {
        this.ranking = ranking;
        this.description = description;
    }
}
