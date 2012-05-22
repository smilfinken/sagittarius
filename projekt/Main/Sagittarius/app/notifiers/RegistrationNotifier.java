package notifiers;

import models.User;
import play.i18n.Messages;
import play.mvc.Mailer;

public class RegistrationNotifier extends Mailer {

	public static void welcome(User user) {
		setSubject(Messages.get("notifiers.registrationnotifier.welcome.subject"));
		addRecipient(String.format("%s %s <%s>", user.firstName, user.surname, user.email));
		setFrom("no-reply@sagittarius.net");
		send(user);
	}
}
