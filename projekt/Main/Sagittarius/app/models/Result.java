/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import javax.persistence.*;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Result extends Model {

    public Result(int pHits, int pTargets) {
        hits = pHits;
        targets = pTargets;
    }
    public int hits;
    public int targets;
}