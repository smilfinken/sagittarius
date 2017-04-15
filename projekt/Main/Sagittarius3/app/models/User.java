package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import play.data.validation.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(nullable = false)
    public String email;

    @Column(nullable = false)
    public String password;

    @Column(nullable = false)
    public String firstName;

    @Column(nullable = false)
    public String lastName;

    @Column(nullable = false)
    public Integer cardId;

    @ManyToMany
    public List<CompetitionSubclass> subclasses;

    @ManyToOne
    public CompetitionRanking ranking;

    @Column(nullable = false)
    public String club;

    public User() {
        this.email = "";
        this.password = "";
        this.firstName = "";
        this.lastName = "";
        this.cardId = null;
        this.subclasses = new ArrayList<>();
        this.ranking = null;
        this.club = "";
    }

    public User(String email, String firstName, String lastName, Integer cardId, CompetitionRanking ranking, String club) {
        this.email = email;
        this.password = "";
        this.firstName = firstName;
        this.lastName = lastName;
        this.cardId = cardId;
        this.subclasses = new ArrayList<>();
        this.ranking = ranking;
        this.club = club;
    }

    public void copyValues(User source) {
        this.email = source.email;
        this.password = source.password;
        this.firstName = source.firstName;
        this.lastName = source.lastName;
        this.cardId = source.cardId;
        this.subclasses = source.subclasses;
        this.ranking = source.ranking;
        this.club = source.club;
    }

    public String fullName() {
        return this.firstName + " " + this.lastName;
    }
}
