/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.ArrayList;
import java.util.List;
import play.mvc.Controller;
import models.*;

/**
 *
 * @author johan
 */
public class Results extends Controller {

    public Results() {
    }

    private static void fakeResults() {
        String newName;
        String newClass;
        ArrayList<Result> newResults = new ArrayList<Result>();

        ResultEntry.deleteAll();
        for (int i = 1; i < 10; i++) {
            newName = "test" + i;
            newClass = "A1";
            newResults.add(new Result(i, 10 - i));
            ResultEntry newResult = new ResultEntry(newName, newClass, newResults);
            newResult.create();
        }
    }

    public static void list() {
        fakeResults();
        List<ResultEntry> results = ResultEntry.all().fetch();

        render(results);
    }

    public static void addResult(String pName, String pClass, ArrayList<Result> pResults) {
        ResultEntry newResult = new ResultEntry(pName, pClass, pResults);

        newResult.create();
    }
}