package common;

import controllers.Secure;
import play.mvc.Controller;
import play.mvc.Http.Header;
import play.mvc.With;

/**
 *
 * @author johan
 */
@With(Secure.class)
public class Checks extends Controller {

	public static boolean checkMSIE(){
		boolean result = false;

		Header userAgent = request.headers.get("user-agent");
		if (userAgent != null){
			result = userAgent.toString().contains("MSIE");
		}

		return result;
	}
}