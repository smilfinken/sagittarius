package controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import play.mvc.Controller;

/**
 *
 * @author johan
 */
public class ErrorReporting extends Controller {

	public static void submitReport(String exStackTrace, String exClass, String exMessage, SimpleDateFormat exDateTime, String extraMessage, String exThreadName, String appVersionCode, String appVersionName, String appPackageName, String devAvailableMemory, String devTotalMemory, String devModel, String devSdk, String devReleaseVersion) {
		File logFile = new File(System.getProperty("java.io.tmpdir"), "sagittariusapp.log");
		try (FileWriter logWriter = new FileWriter(logFile)) {
			logWriter.write(String.format("exStackTrace: %s\nexClass: %s\nexMessage: %s\nexDateTime: %s\nextraMessage: %s\nexThreadName: %s\nappVersionCode: %s\nappVersionName: %s\nappPackageName: %s\ndevAvailableMemory: %s\ndevTotalMemory: %s\ndevModel: %s\ndevSdk: %s\ndevReleaseVersion: %s", exStackTrace, exClass, exMessage, exDateTime, extraMessage, exThreadName, appVersionCode, appVersionName, appPackageName, devAvailableMemory, devTotalMemory, devModel, devSdk, devReleaseVersion));
		} catch (IOException ex) {
			Logger.getLogger(ErrorReporting.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}