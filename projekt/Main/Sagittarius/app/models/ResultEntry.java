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
public class ResultEntry extends Model {

    public ResultEntry(String pName, String pClass, Set<Result> pResults) {
        competitorName = pName;
        competitorClass = pClass;
        competitorResults = pResults;

        for (Result result : pResults) {
            result.create();
        }
    }
    public String competitorName;
    public String competitorClass;
    @OneToMany(cascade = CascadeType.ALL)
    Set<Result> competitorResults;
}