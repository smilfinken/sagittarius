package models;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import play.db.jpa.Model;

/**
 *
 * @author johan
 */
@Entity
public class Competitor extends Model {

    public Competitor(User user) {
        this.user = user;
        this.results = null;
    }

    public Competitor(User user, List<Result> results) {
        this.user = user;
        this.results = results;

        if (results != null) {
            for (Result result : results) {
                result.create();
            }
        }
    }
    @OneToOne
    public User user;
    @OneToMany(cascade = CascadeType.ALL)
    public List<Result> results;

    public String getFullName() {
        return String.format("%s %s", user.firstName, user.surname);
    }

    public String getDivision() {
        return String.format("%s%s", user.category, user.rank);
    }
}