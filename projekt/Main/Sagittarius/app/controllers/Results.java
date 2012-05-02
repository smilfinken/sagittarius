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
        Set<Result> newResults = null;

        ResultEntry.deleteAll();
        for (int i = 1; i < 10; i++) {
            newName = "test" + i;
            newClass = "A1";
            newResults = new HashSet<Result>();
            for (int j = 1; j <= 6; j++) {
                newResults.add(new Result(j, 7-j));
            }
            ResultEntry newResult = new ResultEntry(newName, newClass, newResults);
            newResult.create();
        }
    }

    public static void list() {
        fakeResults();
        List<ResultEntry> results = ResultEntry.all().fetch();

        render(results);
    }

    public static void addResult(String pName, String pClass, Set<Result> pResults) {
        ResultEntry newResult = new ResultEntry(pName, pClass, pResults);

        newResult.create();
    }
}