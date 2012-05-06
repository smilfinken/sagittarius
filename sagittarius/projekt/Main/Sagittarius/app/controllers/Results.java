/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import models.Competitor;
import models.Result;
import org.apache.commons.io.output.FileWriterWithEncoding;
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
        final List<String> classOrder = Arrays.asList("C", "D", "V", "B", "A", "R", "S");

        class ResultListComparator implements Comparator<Competitor> {

            @Override
            public int compare(Competitor A, Competitor B) {
                if (A.competitorClass.equals(B.competitorClass)) {
                    if (sumResults(B.competitorResults) == sumResults(A.competitorResults)) {
                        return sumPoints(B.competitorResults) - sumPoints(A.competitorResults);
                    } else {
                        return sumResults(B.competitorResults) - sumResults(A.competitorResults);
                    }
                } else {
                    String classA = A.competitorClass.substring(0, A.competitorClass.length() - 1);
                    String classB = B.competitorClass.substring(0, B.competitorClass.length() - 1);
                    int classSort = classOrder.indexOf(classA) - classOrder.indexOf(classB);

                    if (classSort != 0) {
                        return classSort;
                    } else {
                        String rankA = A.competitorClass.substring(A.competitorClass.length() - 1);
                        String rankB = B.competitorClass.substring(B.competitorClass.length() - 1);

                        return rankA.compareTo(rankB);
                    }
                }
            }
        }

        List<Competitor> out = pCompetitors;

        ResultListComparator c = new ResultListComparator();
        Collections.sort(out, c);
        return out;
    }

    private static void showResults() {
        List<Competitor> results = Competitor.all().fetch();
        render(sortResults(results));
    }

    private static void deleteEntry(long pID) {
        Competitor entry = Competitor.findById(pID);

        if (entry != null) {
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
                String line = String.format("\"%s\";\"%s\"", competitor.competitorName, competitor.competitorClass);
                int totalHits = 0;
                int totalTargets = 0;
                int totalPoints = 0;

                for (Result result : competitor.competitorResults) {
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