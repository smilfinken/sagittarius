package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.swing.InternalFrameFocusTraversalPolicy;

import play.data.validation.*;
import play.i18n.Messages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

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

    @ManyToMany
    public List<CompetitionClass> allowedClasses;

    public Stage() {
        this.index = 0;
        this.label = "";
        this.targetCount = 0;
        this.points = false;
        this.allowedClasses = new ArrayList<>();
    }

    public Stage(Integer index) {
        this.index = index;
        this.label = "";
        this.targetCount = 0;
        this.points = false;
        this.allowedClasses = new ArrayList<>();
    }

    public void copyValues(Stage source) {
        this.index = source.index;
        this.label = source.label;
        this.targetCount = source.targetCount;
        this.points = source.points;
        this.allowedClasses = source.allowedClasses;
    }

    public ObjectNode toJson() {
        StringBuilder brief = new StringBuilder();

        brief
            .append(Messages.get("description.stagebrief.standing") + Messages.get("description.stagebrief.nosupportinghand") + Messages.get("description.stagebrief.startingangle"));
        if (points) {
            brief.append(System.lineSeparator() + Messages.get("description.stagebrief.points"));
        }

        return Json.newObject()
            .put("id", this.id)
            .put("index", this.index)
            .put("label", this.label)
            .put("targetCount", this.targetCount)
            .put("points", this.points)
            .put("brief", brief.toString());
    }
}
