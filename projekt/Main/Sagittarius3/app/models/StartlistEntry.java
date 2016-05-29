package models;

public class StartlistEntry {
    public String competitor;
    public String club;
    public String competitionClass;
    public String squad;
    public String rollcall;

    StartlistEntry(String competitor, String club, String competitionClass, String squad, String rollcall) {
        this.competitor = competitor;
        this.club = club;
        this.competitionClass = competitionClass;
        this.squad = squad;
        this.rollcall = rollcall;
    }
}
