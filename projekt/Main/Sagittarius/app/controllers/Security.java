package controllers;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import models.User;

public class Security extends Secure.Security {

	static boolean authenticate(String username, String password) {
		boolean result = false;

		User user = User.find("byEmail", username).first();
		if (user != null) {
			result = validate(password, user.password);
		}

		return result;
	}

	static boolean check(String profile) {
		if ("basic".equals(profile)) {
			return true;
		}
		return false;
	}

	static void onDisconnected() {
		Application.index();
	}

	static void onAuthenticated() {
		Application.index();
	}

	public static String hash(String password) {
		String hashedPassword = "";

		if (password != null) {
			try {
				Random random = new Random();
				byte[] salt = new byte[16];
				random.nextBytes(salt);

				SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
				KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 2048, 160);
				byte[] hash = factory.generateSecret(spec).getEncoded();

				String salt16 = String.format("%1$32x", new BigInteger(1, salt)).replace(" ", "0");
				String hash16 = String.format("%1$40x", new BigInteger(1, hash)).replace(" ", "0");

				hashedPassword = String.format("%s%s", salt16, hash16);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			}
		}

		return hashedPassword;
	}

	public static boolean validate(String password, String passwordHash) {
		boolean result = false;

		try {
			byte[] salt = DatatypeConverter.parseHexBinary(passwordHash.substring(0,32));

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 2048, 160);
			byte[] hash = factory.generateSecret(spec).getEncoded();

			String salt16 = String.format("%1$32x", new BigInteger(1, salt)).replace(" ", "0");
			String hash16 = String.format("%1$40x", new BigInteger(1, hash)).replace(" ", "0");

			result = passwordHash.equals(String.format("%s%s", salt16, hash16));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
		}

		return result;
	}
}