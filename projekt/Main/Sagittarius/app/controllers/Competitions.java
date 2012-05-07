package controllers;

import models.Competition;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Competitions extends Controller {

    public static void select(long competitionID) {
        Competition competition = Competition.findById(competitionID);
        render(competition);
    }

    public static void add(String name) {
        Competition competition = new Competition(name);
        render(competition);
    }

    public static void listCompetitors(long competitionID) {
    }

    public static void enroll(long competitionID) {
    }
}