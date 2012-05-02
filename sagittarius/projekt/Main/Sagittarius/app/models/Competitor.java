/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Competitor extends Model {

    public Competitor(String pName, String pClass, List<Result> pResults) {
        competitorName = pName;
        competitorClass = pClass;
        competitorResults = pResults;

        if (pResults != null) {
            for (Result result : pResults) {
                result.create();
            }
        }
    }
//    @Id
//    @GeneratedValue(strategy= GenerationType.IDENTITY)
//    public long competitorID;
    public String competitorName;
    public String competitorClass;
    @OneToMany(cascade = CascadeType.ALL)
    public List<Result> competitorResults;
}