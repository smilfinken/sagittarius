/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.*;
import play.mvc.Controller;
import models.*;

/**
 *
 * @author johan
 */
public class Results extends Controller {

    private static void fakeResults() {
        String newName;
        String newClass;

        for (int i = 1; i < 10; i++) {
            newName = "test" + i;
            newClass = "A1";
            List<Result> newResults = new ArrayList<Result>();
            for (int j = 1; j <= 6; j++) {
                newResults.add(new Result(j, 7 - j));
            }
            addResult(newName, newClass, newResults);
        }
    }

    private static void addResult(String pName, String pClass, List<Result> pResults) {
        Competitor newCompetitor = new Competitor(pName, pClass, pResults);
        newCompetitor.create();
    }

    private static void showResults() {
        List<Competitor> results = Competitor.all().fetch();
        render(results);
    }

    public static void list() {
        fakeResults();
        showResults();
    }

    public static void add(String pName, String pClass, List<Result> pResults) {
        addResult(pName, pClass, pResults);
        showResults();
    }
}