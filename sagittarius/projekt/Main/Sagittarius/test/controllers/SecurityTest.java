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
		String hash = Security.hashPassword(password);

		assertTrue("Password hash does not validate with valid password.", Security.validatePassword(password, hash));
		assertFalse("Password hash validates incorrectly with invalid password.", Security.validatePassword("foo", hash));
	}
	
	@Test
	public void StaticHashAlgorithmTest() {
		String text = "abc123";
		String hash1 = Security.staticHash(text);
		String hash2 = Security.staticHash(text);
		
		assertNotNull(hash1);
		assertTrue(hash1 + " does not match " + hash2, hash1.equals(hash2));
	}
}