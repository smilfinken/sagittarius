package models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.*;

@Entity
public class Competition {
    private static class ScoreSorter implements Comparator<Score> {
        @Override
        public int compare(Score a, Score b) {
            if (a.competitorClass.equals(b.competitorClass)) {
                if (a.scoreTotal() == b.scoreTotal()) {
                    return b.points() - a.points();
                }
                return b.scoreTotal() - a.scoreTotal();
            }
            return a.competitorClass.compareTo(b.competitorClass);
        }
    }

    private static class ClassSorter implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return a.compareTo(b);
        }
    }

    private static class StartlistSorter implements Comparator<StartlistEntry> {
        @Override
        public int compare(StartlistEntry a, StartlistEntry b) {
            return a.club.compareTo(b.club);
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(nullable = false)
    public String label;

    @Column(nullable = true)
    public int squadSize;

    @OneToMany
    public List<Stage> stages;

    @OneToMany
    public List<Squad> squads;

    public Competition() {
        this.label = "";
        stages = new ArrayList<Stage>();
        squads = new ArrayList<Squad>();
    }

    public void copyValues(Competition source) {
        this.label = source.label;
        this.squadSize = source.squadSize;
    }

    public String date() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(calendar.getTime());
    }
    public Integer competitors() {
        Set<Long> ids = new HashSet<>();

        for (Squad squad: squads) {
            for (Competitor competitor: squad.competitors) {
                if (!ids.contains(competitor.user.id)) {
                    ids.add(competitor.user.id);
                }
            }
        }

        return ids.size();
    }

    public Integer entries() {
        Integer result = 0;
        for (Squad squad: squads) {
            result += squad.competitors();
        }
        return result;
    }

    public Integer results() {
        Integer result = 0;
        for (Squad squad: squads) {
            for (Competitor competitor: squad.competitors) {
                if (competitor.scored) {
                    result++;
                }
            }
        }
        return result;
    }

    public ArrayList<StartlistEntry> startlistEntries() {
        ArrayList<StartlistEntry> result = new ArrayList<>();

        for (Squad squad: squads) {
            for (Competitor competitor: squad.competitors) {
                result.add(new StartlistEntry(competitor.fullName(), competitor.clubName(), competitor.combinedClass(), squad.label, squad.rollcall));
            }
        }
        Collections.sort(result, new StartlistSorter());

        return result;
    }

    public Integer maxShots() {
        return 6 * stages.size();
    }

    public Integer maxTargets() {
        Integer result = 0;
        for (Stage stage: stages) {
            result += stage.targetCount;
        }
        return result;
    }

    public Integer maxScore() {
        return maxShots() + maxTargets();
    }

    public Map<String, Integer> classTotals() {
        Map<String, Integer> totals = new TreeMap<>(new ClassSorter());

        for (Squad squad : squads) {
            for (Competitor competitor : squad.competitors) {
                String combinedClass = competitor.combinedClass();
                if (totals.containsKey(combinedClass)) {
                    totals.replace(combinedClass, totals.get(combinedClass) + 1);
                } else {
                    totals.put(combinedClass, 1);
                }
            }
        }

        return totals;
    }

    public ArrayList<ClassResults> calculateResults(HashMap<String, Integer> top, HashMap<String, Integer> limit) {
        ArrayList<Score> scores = new ArrayList<>();
        for (Squad squad : squads) {
            for (Competitor competitor : squad.competitors) {
                if (competitor.scored) {
                    scores.add(new Score(id, competitor));
                }
            }
        }

        // ugly-as-all-feck solution
        ArrayList<Integer> scores_C = new ArrayList<Integer>();
        ArrayList<Integer> scores_B = new ArrayList<Integer>();
        ArrayList<Integer> scores_A = new ArrayList<Integer>();
        ArrayList<Integer> scores_R = new ArrayList<Integer>();

        Collections.sort(scores, new ScoreSorter());
        String currentClassName = "";
        ArrayList<ClassResults> results = new ArrayList<ClassResults>();
        ClassResults classResults = new ClassResults(currentClassName);
        for (Score score : scores) {
            switch (score.competitorCategory) {
                case "C":
                    scores_C.add(score.scoreTotal());
                    break;
                case "B":
                    scores_B.add(score.scoreTotal());
                    break;
                case "A":
                    scores_A.add(score.scoreTotal());
                    break;
                case "R":
                    scores_R.add(score.scoreTotal());
                    break;
            }
            if (!score.competitorClass.equals(currentClassName)) {
                if (classResults.scores.size() != 0) {
                    results.add(classResults);
                }
                currentClassName = score.competitorClass;
                classResults = new ClassResults(currentClassName);
            }
            classResults.scores.add(score);
        }
        if (classResults.scores.size() != 0) {
            results.add(classResults);
        }

        Collections.sort(scores_C, Collections.reverseOrder());
        if (scores_C.size() / 9 > 0) {
            top.put("C", scores_C.get((scores_C.size() / 9) - 1));
        }
        if (scores_C.size() / 3 > 0) {
            limit.put("C", scores_C.get((scores_C.size() / 3) - 1));
        }

        Collections.sort(scores_B, Collections.reverseOrder());
        if (scores_B.size() / 9 > 0) {
            top.put("B", scores_B.get((scores_B.size() / 9) - 1));
        }
        if (scores_B.size() / 3 > 0) {
            limit.put("B", scores_B.get((scores_B.size() / 3) - 1));
        }

        Collections.sort(scores_A, Collections.reverseOrder());
        if (scores_A.size() / 9 > 0) {
            top.put("A", scores_A.get((scores_A.size() / 9) - 1));
        }
        if (scores_A.size() / 3 > 0) {
            limit.put("A", scores_A.get((scores_A.size() / 3) - 1));
        }

        Collections.sort(scores_R, Collections.reverseOrder());
        if (scores_R.size() / 9 > 0) {
            top.put("R", scores_R.get((scores_R.size() / 9) - 1));
        }
        if (scores_R.size() / 3 > 0) {
            limit.put("R", scores_R.get((scores_R.size() / 3) - 1));
        }

        return results;
    }

    public HashMap<Integer, StageStatistics> stageStatistics() {
        HashMap<Integer, StageStatistics> result = new HashMap<>();
        HashMap<Integer, Double> shotTotal = new HashMap<>();
        HashMap<Integer, Double> targetTotal = new HashMap<>();
        Integer count = 0;

        for (Squad squad : squads) {
            for (Competitor competitor : squad.competitors) {
                if (competitor.scored) {
                    for (StageScore score: competitor.scores) {
                        shotTotal.put(score.index, shotTotal.getOrDefault(score.index, 0.0) + score.shots);
                        targetTotal.put(score.index, targetTotal.getOrDefault(score.index, 0.0) + score.targets);
                    }
                    count++;
                }
            }
        }

        for (Integer key: shotTotal.keySet()) {
            result.put(key, new StageStatistics(shotTotal.get(key) / count, targetTotal.get(key) / count));
        }

        return result;
    }
}