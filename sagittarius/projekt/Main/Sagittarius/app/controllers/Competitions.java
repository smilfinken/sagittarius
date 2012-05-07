package controllers;

import models.Competition;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Competitions extends Controller {

    public static void display(long competitionID) {
        Competition competition = Competition.findById(competitionID);
        render(competition);
    }

    public static void listCompetitors(long competitionID) {
    }

    public static void enroll(long competitionID) {
    }
}