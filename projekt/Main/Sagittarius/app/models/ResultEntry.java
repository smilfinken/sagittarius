/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;
import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class ResultEntry extends Model{

    public ResultEntry(String pName, String pClass, ArrayList<Result> pResults) {
        competitorName = pName;
        competitorClass = pClass;
//        competitorResults = pResults;
    }

    public String competitorName;
    public String competitorClass;
    public ArrayList<Result> competitorResults;
}