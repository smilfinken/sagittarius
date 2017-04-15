package models;

import javax.persistence.*;
import play.data.validation.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class CompetitionClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(nullable = false)
    public String label;

    @Column(nullable = false)
    public String description;

    @ManyToMany
    public List<CompetitionSubclass> subclasses;

    public CompetitionClass() {
        this.label = "";
        this.description = "";
        this.subclasses = new ArrayList<>();
    }

    public CompetitionClass(String label, String description) {
        this.label = label;
        this.description = description;
        this.subclasses = new ArrayList<>();
    }
}
