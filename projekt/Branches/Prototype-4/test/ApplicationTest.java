
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class ApplicationTest extends FunctionalTest {

	@Ignore
	public void testThatIndexPageWorks() {
		Response response = GET("/");
		assertIsOk(response);
		assertContentType("text/html", response);
		assertCharset(play.Play.defaultWebEncoding, response);
	}

	@Test
	public void dummyTest() {
		assertTrue(true);
	}
}