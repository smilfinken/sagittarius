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
public class Stage extends Model {

    public void Stage(int targets, boolean hasPoints) {
        this.targets = targets;
        this.hasPoints = hasPoints;
    }
    public int targets;
    public boolean hasPoints;
}