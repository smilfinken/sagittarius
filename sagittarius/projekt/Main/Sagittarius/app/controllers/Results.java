/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import models.Competitor;
import models.Result;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Results extends Controller {

    private static void fakeResults() {
        SecureRandom randomizer = new SecureRandom();

        for (int i = 1; i < 10; i++) {
            String newName = String.format("testperson%03d efternamnsson", randomizer.nextInt(1000) + 1);
            String newClass = String.format("%c%d", (65 + (randomizer.nextInt(3))), (1 + randomizer.nextInt(3)));
            List<Result> newResults = new ArrayList<>();
            for (int j = 1; j <= 6; j++) {
                int hits = randomizer.nextInt(7);
                int targets = 0;
                if (hits > 0) {
                    targets = 1 + randomizer.nextInt(hits);
                }
                int points = 0;
                if (j % 3 == 0) {
                    points = Math.round(randomizer.nextFloat() * (6 - targets) * hits);
                }
                newResults.add(new Result(hits, targets, points));
            }

            addResult(newName, newClass, newResults);
        }
    }

    private static void addResult(String pName, String pClass, List<Result> pResults) {
        Competitor newCompetitor = new Competitor(pName, pClass, pResults);
        newCompetitor.create();
    }

    private static int sumPoints(List<Result> pResults) {
        int points = 0;

        for (Result result : pResults) {
            points += result.points;
        }

        return points;
    }

    private static int sumResults(List<Result> pResults) {
        int hits = 0;
        int targets = 0;

        for (Result result : pResults) {
            hits += result.hits;
            targets += result.targets;
        }

        return hits + targets;
    }

    private static List<Competitor> sortResults(List<Competitor> pCompetitors) {
        class NameComparator implements Comparator<Competitor> {

            @Override
            public int compare(Competitor A, Competitor B) {
                if (A.competitorClass.equals(B.competitorClass)) {
                    if (sumResults(B.competitorResults) == sumResults(A.competitorResults)) {
                        return sumPoints(B.competitorResults) - sumResults(A.competitorResults);
                    } else {
                        return sumResults(B.competitorResults) - sumResults(A.competitorResults);
                    }
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

    private static void deleteEntry(long pID){
        Competitor entry = Competitor.findById(pID);

        if (entry != null){
            entry.delete();
        }
    }

    public static void list() {
//        fakeResults();
        showResults();
    }

    public static void add(String pName, String pClass, List<Result> pResults) {
        if (pName != null && pName.length() != 0) {
            addResult(pName, pClass, pResults);
        }
        showResults();
    }

    public static void delete(long pID) {
        if (pID > 0){
            deleteEntry(pID);
        }
        showResults();
        render();
    }

    public static void enter() {
        render();
    }
}