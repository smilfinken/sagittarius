package models;

import org.joda.time.DateTime;
import org.joda.time.format.*;

import javax.persistence.*;
import play.data.validation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Squad {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(nullable = false)
    public String label;

    @Column(nullable = false)
    public int slots;

    @Column(nullable = true)
    public String rollcall;

    @OneToMany
    public List<Competitor> competitors;

    public Squad() {
        this.label = "";
        this.slots = 0;
        this.rollcall = "";
        this.competitors = new ArrayList<Competitor>();
    }

    public Squad(Integer slots) {
        this.label = "";
        this.slots = slots;
        this.rollcall = "";
        this.competitors = new ArrayList<Competitor>();
    }

    public void copyValues(Squad source) {
        this.label = source.label;
        this.slots = source.slots;
        this.rollcall = source.rollcall;
    }

    public int competitors() {
        return this.competitors.size();
    }

    public boolean scored() {
        for (Competitor competitor : this.competitors) {
            if (!competitor.scored) {
                return false;
            }
        }
        return true;
    }

    public DateTime rollcallTime() {
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
            return formatter.parseDateTime(rollcall);
        } catch (Exception ex) {
            return new DateTime();
        }
    }
}
