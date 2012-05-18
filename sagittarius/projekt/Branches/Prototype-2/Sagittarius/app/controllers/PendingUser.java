	package controllers;

import models.User;
import play.data.validation.Required;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class PendingUser extends Controller {

    public static void signup() {
    	render();
    }

    public static void doSignup(@Required String firstname, @Required String surname, String cardnumber, @Required String email, @Required String password, @Required String passwordVerification) {
    	if (!password.equals(passwordVerification)) {
    		/*TODO: Store in flash and redirect to posting page*/
    		signup();
    	}
    	User user = new User( firstname,  surname,  cardnumber, email,  password);
    	user.create();
    	Security.authenticate(email, password);
    	Application.index();
    }
}