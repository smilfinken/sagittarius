/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author johan
 */
public class Formatting {

	public static String dateToStartTime(Date date) {
		String result = "";

		DateFormat df = new SimpleDateFormat("HH:mm");
		result = df.format(date);

		return result;
	}

	public static Date timeStringToDate(String time) {
		Date result = new Date();

		try {
			DateFormat df = new SimpleDateFormat("HH:mm");
			result = df.parse(time);
		} catch (Exception e) {
		}

		return result;
	}
}
