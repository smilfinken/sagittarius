/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.*;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class ResultEntry extends Model {
    public ResultEntry(String pName, String pClass, Set<String> pResults) {
        competitorName = pName;
        competitorClass = pClass;
        competitorResults = pResults;
    }

    public String competitorName;
    public String competitorClass;
    @OneToMany
    Set<String> competitorResults;
}