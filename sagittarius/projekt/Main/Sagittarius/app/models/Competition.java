package models;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Competition extends Model {

    public void Competition(String name, CompetitionType type) {
        this.name = name;
        this.type = type;
        this.stages = null;
    }

    public void Competition(String name, CompetitionType type, List<Stage> stages) {
        this.name = name;
        this.type = type;
        this.stages = stages;
    }
    public String name;
    public CompetitionType type;
    @OneToMany(cascade = CascadeType.ALL)
    public List<Stage> stages;
}