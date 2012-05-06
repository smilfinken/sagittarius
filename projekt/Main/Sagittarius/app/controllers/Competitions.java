package controllers;

import models.Competition;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Competitions extends Controller {

    public static void Display(long competitionID) {
        Competition competition = Competition.findById(competitionID);
        render(competition);
    }
}