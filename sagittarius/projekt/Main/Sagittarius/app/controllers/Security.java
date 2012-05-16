package controllers;

import models.*;

public class Security extends Secure.Security {

    static boolean authenticate(String username, String password) {
        return VerifiedUser.connect(username, password) != null;
    }
    
    static boolean check(String profile) {
        if("basic".equals(profile)) {
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
    
}