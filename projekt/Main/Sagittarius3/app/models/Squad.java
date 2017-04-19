package models;

import play.Logger;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

import javax.persistence.*;
import play.data.validation.*;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

@Entity
public class Squad implements Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(nullable = false)
    public String label;

    @Column(nullable = false)
    public int slots;

    @Column(nullable = true)
    public DateTime rollcall;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Competitor> competitors;

    @ManyToMany
    public List<CompetitionClass> allowedClasses;

    public Squad() {
        this.label = "";
        this.slots = 0;
        this.rollcall = null;
        this.competitors = new ArrayList<>();
        this.allowedClasses = new ArrayList<>();
    }

    public Squad(Integer slots) {
        this.label = "";
        this.slots = slots;
        this.rollcall = null;
        this.competitors = new ArrayList<>();
        this.allowedClasses = new ArrayList<>();
    }

    @Override
    public int compareTo(Object o) {
        DateTime a = this.rollcall;
        DateTime b = ((Squad)o).rollcall;
        return (a != null && b != null) ? a.compareTo(b) : 0;
    }

    public void copyValues(Squad source) {
        this.label = source.label;
        this.slots = source.slots;
        this.rollcall = source.rollcall;
        this.allowedClasses = source.allowedClasses;
    }

    public int competitors() {
        return this.competitors.size();
    }

    public boolean available() {
        return competitors.size() < slots;
    }

    public boolean scored() {
        for (Competitor competitor : this.competitors) {
            if (!competitor.scored()) {
                return false;
            }
        }
        return competitors() > 0;
    }

    public String rollcallString() {
        return rollcall.toString("HH:mm");
    }

    public String allowedClassesString() {
        String result = "";

        for (CompetitionClass competitionClass : this.allowedClasses) {
            result += competitionClass.label + " ";
        }

        return result.trim();
    }

    public ObjectNode toJson() {
        return Json.newObject()
            .put("id", this.id)
            .put("label", this.label)
            .put("rollcall", this.rollcallString())
            .put("count", this.competitors.size())
            .put("scored", this.scored());
    }
}
