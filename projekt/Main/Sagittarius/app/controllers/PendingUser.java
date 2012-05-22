	package controllers;

import models.User;
import play.data.validation.Email;
import play.data.validation.Required;
import play.i18n.Messages;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class PendingUser extends Controller {

    public static void signup() {
    	render();
    }

    public static void doSignup(@Required String firstname, @Required String surname, String cardnumber, @Required @Email String email, @Required String password, @Required String passwordVerification) {
    	validation.equals(Messages.get("controller.pendinguser.password"), password, Messages.get("controller.pendinguser.passwordVerification"), passwordVerification);
    	if(validation.hasErrors()) {
    	       params.flash(); // add http parameters to the flash scope
    	       validation.keep(); // keep the errors for the next request
    	       signup();
    	}
    	
    	User user = new User( firstname,  surname,  cardnumber, email,  password);
    	user.create();
    	Security.authenticate(email, password);
    	Application.index();
    }
}