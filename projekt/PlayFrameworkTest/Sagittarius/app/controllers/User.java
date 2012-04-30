/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.List;
import play.mvc.Controller;

/**
 *
 * @author ancla
 */
public class User extends Controller {
/*    
POST    /user                                   User.create
GET     /user                                   User.list
GET     /user/{id}                              User.read
PUT     /user/{id}                              User.update
DELETE  /user/{id}                              User.delete    
*/
    public static void create(String userName, String firstName, String lastName, String eMail) {
        if (userName == null) {
            render();
        }
        models.User user;
        user = new models.User(userName, firstName, lastName, eMail);
        user.create();
        flash.success("The user has been created!");                
        list();
    }
    public static void read(Long userId) {
        models.User user = models.User.findById(userId);
        notFoundIfNull(user);
        render("app/views/User/edit.html", user);
    }
    public static void update(Long userId, String userName, String firstName, String lastName, String eMail) {
        models.User user = models.User.findById(userId);
        notFoundIfNull(user);
        user.firstName = firstName != null ? firstName : user.firstName;
        user.lastName = lastName != null ? lastName : user.lastName;
        user.eMail = eMail != null ? eMail : user.eMail;
        user.save();
        flash.success("The user has been updated!");
        list();
    }
    public static void delete(Long userId) {
        if (userId != null) {
            models.User.delete("id=?", userId);
        }
        flash.success("The user has been deleted!");
        list();    
    }
    public static void list() {
        List<models.User> users = models.User.all().fetch();
        System.out.println("Listing all users: " + users.size());
        render(users);
    }
}
