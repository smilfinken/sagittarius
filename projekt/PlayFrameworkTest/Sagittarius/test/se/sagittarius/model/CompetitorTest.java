/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.sagittarius.model;

import java.util.List;
import models.Competitor;
import models.Club;
import org.junit.Before;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 *
 * @author ancla
 */
public class CompetitorTest extends UnitTest {

    Competitor competitor;
    
    @Before
    public void setUp() {
        Fixtures.deleteDatabase();
        Club club = new Club("Mölndals Skytteförening");
        club.create();
        competitor = new Competitor("first name", "last name", club, Competitor.CompetingClass.ONE);
    }

    @Test
    public void testReadWriteCompetitor(){
        this.competitor.create();
        assertEquals(this.competitor, Competitor.findById(this.competitor.id));
    }
    
    @Test
    public void updateCompetitor() {
        this.competitor.create();

        Club club = new Club("Partille Skytteförening");
        club.create();
        Competitor persistedCompetitor = Competitor.findById(this.competitor.id);
        persistedCompetitor.firstName = "new first name";
        persistedCompetitor.lastName = "new last name";
        persistedCompetitor.competingClass = Competitor.CompetingClass.THREE;
        persistedCompetitor.club = club;
        persistedCompetitor.save();
        
        List<Competitor> competitors = Competitor.findAll();
        boolean competitorIsUpdated = false;
        for (Competitor c : competitors) {
            System.out.println("Iterating through " + c);
            if (c.id == this.competitor.id) {
                assertEquals(this.competitor, c);
                competitorIsUpdated = true;
            }
        }
        assertTrue("No matching Competitor found in the database, update failed?",
                competitorIsUpdated);
    }
}
