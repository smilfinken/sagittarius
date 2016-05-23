package models;

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
    public int cardId;

    @Column(nullable = false)
    public boolean female;

    @Column(nullable = false)
    public boolean junior;

    @Column(nullable = false)
    public boolean senior;

    @Column(nullable = false)
    public boolean supersenior;

    @Column(nullable = false)
    public int ranking;

    @Column(nullable = false)
    public String club;

    public User() {
        this.email = "";
        this.password = "";
        this.firstName = "";
        this.lastName = "";
        this.cardId = 0;
        this.female = false;
        this.junior = false;
        this.senior = false;
        this.supersenior = false;
        this.ranking = 2;
        this.club = "";
    }

    public void copyValues(User source) {
        this.email = source.email;
        this.password = source.password;
        this.firstName = source.firstName;
        this.lastName = source.lastName;
        this.cardId = source.cardId;
        this.female = source.female;
        this.junior = source.junior;
        this.senior = source.senior;
        this.supersenior = source.supersenior;
        this.ranking = source.ranking;
        this.club = source.club;
    }

    public String fullName() {
        return this.firstName + " " + this.lastName;
    }
}
