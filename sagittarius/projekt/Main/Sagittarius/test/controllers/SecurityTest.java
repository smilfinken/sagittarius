package controllers;

import org.junit.Test;
import play.test.UnitTest;

/**
 *
 * @author johan
 */
public class SecurityTest extends UnitTest {

	@Test
	public void HashAlgorithmTest(){
		String password = "abc123";
		String hash = Security.hash(password);

		assertTrue("Password hash does not validate with valid password.", Security.validate(password, hash));
		assertFalse("Password hash validates incorrectly with invalid password.", Security.validate("foo", hash));
	}
}