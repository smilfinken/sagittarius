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
    
    public static void edit(Long userId, String userName, String firstName, String lastName, String eMail) {
        models.User user;
        if(userId != null) {
            user = models.User.findById(userId);
            notFoundIfNull(user);
            
            if (userName != null) {
                // Edit exising user
                user.firstName = firstName != null ? firstName : user.firstName;
                user.lastName = lastName != null ? lastName : user.lastName;
                user.eMail = eMail != null ? eMail : user.eMail;
                user.save();
                flash.success("The user has been updated!");
                list();
            } else {
                render(user);
            }
        }
        list();
    }
    
    public static void add(boolean save, String userName, String firstName, String lastName, String eMail) {
        models.User user;
        if (save){
            
            // Add new user
            user = new models.User(userName, firstName, lastName, eMail);
            user.create();
            flash.success("The user has been created!");                
            list();
        } else {
            render();
        }    
    }
    
    public static void list() {
        List<models.User> users = models.User.all().fetch();
        System.out.println("Listing all users: " + users.size());
        render(users);
    }
    
    public static void delete(Long userId){
        if (userId != null) {
            models.User.delete("id=?", userId);
        }
        list();
    }
}
