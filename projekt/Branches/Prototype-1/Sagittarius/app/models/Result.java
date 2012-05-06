/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Result extends Model {

    public Result(int pHits, int pTargets, int pPoints) {
        hits = pHits;
        targets = pTargets;
        points = pPoints;
    }
    public int hits;
    public int targets;
    public int points;
}