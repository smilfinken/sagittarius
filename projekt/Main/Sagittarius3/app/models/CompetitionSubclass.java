package models;

import javax.persistence.*;
import play.data.validation.*;

@Entity
public class CompetitionSubclass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(nullable = false)
    public String label;

    @Column(nullable = false)
    public String description;

    public CompetitionSubclass() {
        this.label = "";
        this.description = "";
    }
    public CompetitionSubclass(String label, String description) {
        this.label = label;
        this.description = description;
    }
}
