/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Stage extends Model {

    public Stage(int targets) {
        this.targets = new ArrayList<>(targets);
    }
    @ManyToMany
    public List<Target> targets;

    public int targetCount() {
        if (targets != null) {
            return targets.size();
        } else {
            return 0;
        }
    }

    public boolean hasPoints() {
        for (Target target : targets) {
            if (target.hasPoints) {
                return true;
            }
        }
        return false;
    }
}