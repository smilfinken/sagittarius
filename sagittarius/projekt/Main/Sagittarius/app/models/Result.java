/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Result extends Model {
    public Result(String pCompetitor, int pHits, int pTargets){
        competitor = pCompetitor;
        hits = pHits;
        targets = pTargets;
    }

    @ManyToOne
    public String competitor;
    public int hits;
    public int targets;
}