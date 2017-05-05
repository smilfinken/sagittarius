package net.smilfinken.sagittarius;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

class Utils {
    public static String getJSONString(JSONObject item, String key) {
        String result = "";
        try {
            result = item.getString(key);
        } catch (JSONException exception) {
            Logger.getGlobal().log(Level.SEVERE, "unable to get info from item: " + exception.getLocalizedMessage());
        } finally {
            return result;
        }
    }

    public static Integer getJSONInteger(JSONObject item, String key) {
        Integer result = 0;
        try {
            result = item.getInt(key);
        } catch (JSONException exception) {
            Logger.getGlobal().log(Level.SEVERE, "unable to get info from item: " + exception.getLocalizedMessage());
        } finally {
            return result;
        }
    }
}
