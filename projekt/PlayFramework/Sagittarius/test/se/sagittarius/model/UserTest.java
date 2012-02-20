/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.sagittarius.model;

import java.util.List;
import javax.persistence.EntityManager;
import models.User;
import net.sf.oval.constraint.Email;
import org.junit.Before;
import org.junit.Test;
import play.db.jpa.JPA;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 *
 * @author ancla
 */
public class UserTest extends UnitTest {
    
    User user;

    @Before
    public void setUp() {
        Fixtures.deleteDatabase();
        user = new User("username", "first name", "last name", "address", "telephone number");
    }
    
    @Test
    public void testReadWriteUser() {
        user.create();
        assertEquals(user, User.findById(user.id));
    }
    
    @Test
    public void testDeleteUser() {
        user.create();
        User storedUser = User.findById(user.id);
        storedUser.delete();
        
        // Loop through all persisted entities
        List<User> users = User.all().fetch();
        for (User u : users) {
            if (u.id == user.id) {
                assertTrue(u.deleted);
            }
        }
    }
    
    @Test
    public void testReadUserFromStorageUsingNamedQuery() {
        
        // Persist user
        JPA.em().persist(user);
        JPA.em().flush();
                
        // Verify insertion
        List<User> users = JPA.em().createNamedQuery("getUserByUsername")
                .setParameter("username", this.user.userName).getResultList();
        if (users != null && users.size() > 0) {
            assertEquals(this.user, users.get(0));
        } else {
            fail("Failed to read persisted User from user");
        }
    } 
}
