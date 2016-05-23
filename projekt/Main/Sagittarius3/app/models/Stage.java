package models;

import javax.persistence.*;
import javax.swing.InternalFrameFocusTraversalPolicy;

import play.data.validation.*;

@Entity
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(nullable = false)
    public int index;

    @Column(nullable = false)
    public String label;

    @Column(nullable = false)
    public int targetCount;

    @Column(nullable = false)
    public boolean points;

    public Stage() {
        this.index = 0;
        this.label = "";
        this.targetCount = 0;
        this.points = false;
    }

    public Stage(Integer index) {
        this.index = index;
        this.label = "";
        this.targetCount = 0;
        this.points = false;
    }

    public void copyValues(Stage source) {
        this.index = source.index;
        this.label = source.label;
        this.targetCount = source.targetCount;
        this.points = source.points;
    }
}