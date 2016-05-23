package models;

import java.util.List;
import java.util.ArrayList;

public class ClassResults {
    public String className;
    public List<Score> scores;

    public ClassResults(String className) {
        this.className = className;
        this.scores = new ArrayList<Score>();
    }
}
