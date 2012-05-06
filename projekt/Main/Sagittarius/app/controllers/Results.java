/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import models.Competitor;
import models.Result;
import models.User;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class Results extends Controller {

    private static void addResult(User user, List<Result> results) {
        Competitor newCompetitor = new Competitor(user, results);
        newCompetitor.create();
    }

    private static int sumPoints(List<Result> results) {
        int points = 0;

        for (Result result : results) {
            points += result.points;
        }

        return points;
    }

    private static int sumResults(List<Result> results) {
        int hits = 0;
        int targets = 0;

        for (Result result : results) {
            hits += result.hits;
            targets += result.targets;
        }

        return hits + targets;
    }

    private static List<Competitor> sortResults(List<Competitor> competitors) {
        final List<String> classOrder = Arrays.asList("C", "D", "V", "B", "A", "R", "S");

        class ResultListComparator implements Comparator<Competitor> {

            @Override
            public int compare(Competitor A, Competitor B) {
                if (A.getDivision().equals(B.getDivision())) {
                    if (sumResults(B.results) == sumResults(A.results)) {
                        return sumPoints(B.results) - sumPoints(A.results);
                    } else {
                        return sumResults(B.results) - sumResults(A.results);
                    }
                } else {
                    String classA = A.getDivision().substring(0, Math.max(1, A.getDivision().length() - 1));
                    String classB = B.getDivision().substring(0, Math.max(1, B.getDivision().length() - 1));
                    int classSort = classOrder.indexOf(classA) - classOrder.indexOf(classB);

                    if (classSort != 0) {
                        return classSort;
                    } else {
                        String rankA = A.getDivision().substring(A.getDivision().length() - 1);
                        String rankB = B.getDivision().substring(B.getDivision().length() - 1);

                        return rankA.compareTo(rankB);
                    }
                }
            }
        }

        List<Competitor> out = competitors;

        ResultListComparator c = new ResultListComparator();
        Collections.sort(out, c);
        return out;
    }

    private static void showResults() {
        List<Competitor> results = Competitor.all().fetch();
        render(sortResults(results));
    }

    private static void deleteEntry(long competitorID) {
        Competitor entry = Competitor.findById(competitorID);

        if (entry != null) {
            entry.delete();
        }
    }

    public static void list() {
        showResults();
    }

    public static void add(int userID, List<Result> results) {
        User user = User.findById(userID);
        if (user != null) {
            addResult(user, results);
        }
        showResults();
    }

    public static void delete(long pID) {
        if (pID > 0) {
            deleteEntry(pID);
        }
        showResults();
        render();
    }

    public static void enter() {
        render();
    }

    public static void export() throws IOException {
        List<Competitor> results = Competitor.all().fetch();

        File resultsFile = File.createTempFile("sgt", "csv");
        resultsFile.deleteOnExit();

        try (FileWriter resultsWriter = new FileWriter(resultsFile)) {
            String header1 = ";";
            String header2 = "Namn;Klass";
            for (int i = 1; i <= 6; i++) {
                header1 += String.format(";\"Station %d\";;", i);
                header2 += String.format(";\"Träff\";\"Figurer\";\"Poäng\"", i);
            }
            header1 += ";Summa;;;;\n";
            header2 += ";\"Träff\";\"Figurer\";\"Totalt\";\"Poäng\"\n";
            resultsWriter.write(header1 + header2);

            for (Competitor competitor : sortResults(results)) {
                String line = String.format("\"%s\";\"%s\"", competitor.getFullName(), competitor.getDivision());
                int totalHits = 0;
                int totalTargets = 0;
                int totalPoints = 0;

                for (Result result : competitor.results) {
                    line += String.format(";%d;%d;%d", result.hits, result.targets, result.points);
                    totalHits += result.hits;
                    totalTargets += result.targets;
                    totalPoints += result.points;
                }

                line += String.format(";%d;%d;%d;%d\n", totalHits, totalTargets, totalHits + totalTargets, totalPoints);
                resultsWriter.write(line);
            }
        }

        response.setHeader("Content-Length", String.format("%d", resultsFile.length()));
        response.setHeader("Content-Type", "text/csv; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=out.csv");
        renderBinary(resultsFile);
    }
}