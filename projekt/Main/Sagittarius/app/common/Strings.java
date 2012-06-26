/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 *
 * @author johan
 */
public class Strings {

	public static String capitaliseFirst(String line) {
		if (line == null || line.length() == 0) {
			return line;
		} else {
			StringBuilder result = new StringBuilder(line);
			result.setCharAt(0, Character.toUpperCase(result.charAt(0)));
			return result.toString();
		}
	}
}
