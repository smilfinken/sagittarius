package controllers;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import models.User;

public class Security extends Secure.Security {

	static boolean authenticate(String username, String password) {
		return User.connect(username, password);
	}

	static boolean check(String profile) {
		if ("admin".equals(profile)) {
			return User.check(connected(), profile);
		}
		return false;
	}

	static void onDisconnected() {
		Application.index();
	}

	static void onAuthenticated() {
		Application.index();
	}
	
	public static String staticHash(String text) {

		// currentTimeMillis - To ensure a failing hashing algoritm does not allow for registration check fall through
		String hashedText = String.valueOf(System.currentTimeMillis());
		
		if (text != null) {
			try {
				SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
				KeySpec spec = new DESKeySpec("ARandomString".getBytes());
				byte[] hash = factory.generateSecret(spec).getEncoded();
				hashedText = String.format("%1$16x", new BigInteger(1, hash)).replace(" ", "0");
			} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
				e.printStackTrace();
			}
		}
		
		return hashedText;
	}	

	static byte[] generateHash(byte[] salt, String password) {
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 2048, 160);

			return factory.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			return null;
		}
	}

	static String toHex(byte[] salt, byte[] hash) {
		String salt16 = String.format("%1$32x", new BigInteger(1, salt)).replace(" ", "0");
		String hash16 = String.format("%1$40x", new BigInteger(1, hash)).replace(" ", "0");

		return String.format("%s%s", salt16, hash16);
	}

	public static String hashPassword(String password) {
		String hashedPassword = "";

		if (password != null) {
			Random random = new Random();
			byte[] salt = new byte[16];
			random.nextBytes(salt);
			hashedPassword = toHex(salt, generateHash(salt, password));
		}

		return hashedPassword;
	}

	public static boolean validatePassword(String password, String passwordHash) {
		byte[] salt = DatatypeConverter.parseHexBinary(passwordHash.substring(0, 32));

		return passwordHash.equals(toHex(salt, generateHash(salt, password)));
	}
}