import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class ApplicationTest extends FunctionalTest {

    @Test
    public void testThatIndexPageWorks() {
        Response response = GET("/");
        assertValidHtml( response);
    }

    @Test
    public void testThatHandleUsersPageWorks() {
        Response response = GET("/addUser");
        assertValidHtml(response);
    }
    
    private void assertValidHtml(Response response) {
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset(play.Play.defaultWebEncoding, response);
    }
}