package notifiers;

import models.User;
import play.i18n.Messages;
import play.mvc.Mailer;

public class RegistrationNotifier extends Mailer {

	public static void welcome(User user) {
		setSubject(Messages.get("notifiers.RegistrationNotifier.welcome.subject"));
		addRecipient(user.email);
		setFrom("no-reply@sagittarius.net");
		send(user);
	}
}
