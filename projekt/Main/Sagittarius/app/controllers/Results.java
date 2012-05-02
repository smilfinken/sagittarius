/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.security.SecureRandom;
import java.util.*;
import play.mvc.Controller;
import models.*;

/**
 *
 * @author johan
 */
public class Results extends Controller {

    private static void fakeResults() {
        SecureRandom randomizer = new SecureRandom();

        for (int i = 1; i < 10; i++) {
            String newName = "test" + (int) (randomizer.nextInt(10) + 1);
            String newClass = String.format("%c%d", (65 + (randomizer.nextInt(3))), (1 + randomizer.nextInt(3)));
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

    private static List<Competitor> sortResults(List<Competitor> pCompetitors) {
        class NameComparator implements Comparator<Competitor> {

            public int compare(Competitor A, Competitor B) {
                if (A.competitorClass.equals(B.competitorClass)) {
                    return (A.competitorName.compareTo(B.competitorName));
                } else {
                    return (A.competitorClass.compareTo(B.competitorClass));
                }
            }
        }

        List<Competitor> out = pCompetitors;

        NameComparator nc = new NameComparator();
        Collections.sort(out, nc);
        return out;
    }

    private static void showResults() {
        List<Competitor> results = Competitor.all().fetch();
        render(sortResults(results));
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