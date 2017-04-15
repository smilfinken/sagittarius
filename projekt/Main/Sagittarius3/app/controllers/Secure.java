package controllers;

import play.libs.Json;
import views.html.*;

//import play.api.libs.mailer._;
import javax.inject.Inject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

public class Secure extends Controller {
    //@Inject
    //private MailerClient mailerClient;

    @Inject
    private FormFactory formFactory;

    private static class Token {
        public String access_token;
        public String token_type;
        public Integer expires_in;
        public String refresh_token;
    }

    private static class Claim {
        public String Type;
        public String Value;
        public String Issuer;
    }

    private static class Resource {
        public Long Id;
        public String UserName;
        public String Password;
        public String Salt;
        public List<Claim> Claims;
    }

    public Result login() {
        return ok(loginform.render());
    }

    /*
     *

        POST https://opspikenetauthsrv.azurewebsites.net/Token HTTP/1.1
        Authorization: Basic OUI2RjMwQ0EtRDkyMy00OEZELThCN0UtODNCRjFEMkREMkNFOg==
        Accept: application/x-www-form-urlencoded
        Content-Type: application/x-www-form-urlencoded
        Host: opspikenetrc1.azurewebsites.net
        Content-Length: 71
        Expect: 100-continue
        Connection: Keep-Alive

        grant_type=password&username=user%40azure.com&password=1234&scope=write

     *
     *
     *

        {
            "access_token":"eyJhbGciOiJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGRzaWctbW9yZSNyc2Etc2hhMjU2IiwieDV0IjoibFJMXzlZVTY5WXJ4UTBUWDc1aWxxVFZtMldFIiwidHlwIjoiSldUIn0.eyJuYmYiOjE0NjUxMDk2NjUsImV4cCI6MTQ2NTEwOTcyNSwidXNlcm5hbWUiOiJ1c2VyQGF6dXJlLmNvbSIsInNjb3BlIjoid3JpdGUiLCJpc3MiOiJ1cm46YXV0aG9yaXphdGlvbnNlcnZlciIsImF1ZCI6InVybjpyZXNvdXJjZXNlcnZlciJ9.lT0aua8-eTA1L1OR0NP8B5alW92r2f2pfGrhUVPeYbwOpmT3f5w6jEAby0FOI_w27gere4ZSXSxWID5nowr6Z5jR2gm9cj0XDYtDZoYfcHDMC0H5_lPEirwjlfHMH_Ici3zKfwVJFjgU8g4buN5E78gVYwCJDNCjt6A0H_q8nX4miZh0ryC6OGT6I36Zt3Eyp2tJ_9SCgEGMvA8jVaA3Z9MmnEg_HwMJI1VdPRaW9B25iY81q3Jt7XbyHRvvytHvh_SVXVxB2M2Ymv3HAC2J-tumQOLAslmslSej0whCwda2aHGCvty37VNHRPwEakIgScazPWPFJjSJv_qPVzZn1g",
            "token_type":"Bearer",
            "expires_in":60,
            "refresh_token":"PMGieWJR16eM3rIWVviXQRrvKMFfiyIkrSWBTMzui7BO4JEUFWi1CsneliekNhlUccNzsFNP9So+LvAQc/+1TGuedGy5EUnnuYT4ywwyFvg99jtqHTE="
        }

     *
     *
     *

        {
            "Id":1,
            "UserName":"user@azure.com",
            "Password":null,
            "Salt":null,
            "Claims":[
                {
                    "Type":"username",
                    "Value":"user@azure.com",
                    "Issuer":null
                }
            ]
        }

     *
     */
    public Result doLogin() {
        DynamicForm dynamicData = formFactory.form().bindFromRequest();
        String username = dynamicData.get("username"); // user@azure.com
        String password = dynamicData.get("password"); // 1234

        try {
            String authUrl = "https://opspikenetauthsrv.azurewebsites.net/Token";
            String charset = "UTF-8";
            String query = String.format("grant_type=password&username=%s&password=%s&scope=write", username, password);

            URLConnection connection = new URL(authUrl).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic OUI2RjMwQ0EtRDkyMy00OEZELThCN0UtODNCRjFEMkREMkNFOg==");
            connection.setRequestProperty("Host", "opspikenetrc1.azurewebsites.net");

            try (OutputStream output = connection.getOutputStream()) {
                output.write(query.getBytes(charset));
            };

            Integer status = ((HttpURLConnection)connection).getResponseCode();
            String result = "";
            List<String> claims = new ArrayList<>();
            if (status == 200) {
                Token token = Json.fromJson(Json.parse(connection.getInputStream()), Token.class);

                String resourceUrl = "https://opnetspikeusercredserverrc1.azurewebsites.net/UserCredentials/user@azure.com";
                connection = new URL(resourceUrl).openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Authorization", String.format("Bearer %s", token.access_token));

                status = ((HttpURLConnection) connection).getResponseCode();
                if (status == 200) {
                    //Resource resource = Json.fromJson(Json.parse(connection.getInputStream()), Resource.class);
                    //result = String.format("id: %d, username: %s, #claims: %d", resource.Id, resource.UserName, claims.size());
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(((HttpURLConnection) connection).getInputStream(), charset))) {
                        for (String line; (line = reader.readLine()) != null; ) {
                            result += line;
                        }
                    }
                } else {
                    result = "Failed to access resource";
                }
            } else {
                result = "Failed to get authorization";
            }

            return ok(loggedin.render(status, result, claims));
        } catch (Exception e) {
            return ok(loggedin.render(666, String.format("Epic fail: %s", e.getMessage()), new ArrayList<String>()));
        }
    }
}
