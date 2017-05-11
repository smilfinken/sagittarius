package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ser.std.StdArraySerializers;

import models.CompetitionRanking;
import models.CompetitionSubclass;
import models.User;
import views.html.*;

import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.Query;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.TreeMap;

public class Users extends Controller {
    @Inject
    private FormFactory formFactory;

    @Transactional(readOnly = true)
    public Result list() {
        ArrayList<User> users = (ArrayList<User>)JPA.em().createQuery("SELECT u FROM User u ORDER BY club, lastName, firstName", User.class).getResultList();

        TreeMap<String, ArrayList<User>> usersByClub = new TreeMap<>();
        if (!users.isEmpty()) {
            String currentClub = users.get(0).club;
            ArrayList<User> clubUsers = new ArrayList<>();
            for (User user : users) {
                if (user.club.compareTo(currentClub) != 0) {
                    usersByClub.put(currentClub, clubUsers);
                    clubUsers = new ArrayList<>();
                    currentClub = user.club;
                }
                clubUsers.add(user);
            }
            usersByClub.put(currentClub, clubUsers);
        }

        return ok(userlist.render(usersByClub, users.size()));
    }

    @Transactional
    public Result edit(Long userId) {
        ArrayList<CompetitionSubclass> subclasses = (ArrayList<CompetitionSubclass>)JPA.em().createQuery("SELECT c FROM CompetitionSubclass c ORDER BY label", CompetitionSubclass.class).getResultList();
        ArrayList<CompetitionRanking> rankings = (ArrayList<CompetitionRanking>)JPA.em().createQuery("SELECT c FROM CompetitionRanking c ORDER BY ranking", CompetitionRanking.class).getResultList();
        if (userId != 0) {
            return ok(useredit.render(JPA.em().find(User.class, userId), subclasses, rankings));
        } else {
            return ok(useredit.render(new User("", "", "", null, null, ""), subclasses, rankings));
        }
    }

    @Transactional
    public Result save() {
        User requestData = formFactory.form(User.class).bindFromRequest().get();
        User persistedData = JPA.em().find(User.class, requestData.id);

        if (persistedData != null) {
            persistedData.copyValues(requestData);
        } else {
            persistedData = JPA.em().merge(requestData);
        }

        DynamicForm dynamicData = formFactory.form().bindFromRequest();
        //Logger.debug(dynamicData.toString());
        persistedData.ranking = JPA.em().find(CompetitionRanking.class, new Long(dynamicData.data().get("rankingId")));
        // the following is a completely ridiculous solution, but it's the only one I can get to work properly
        for (String key: dynamicData.data().keySet()) {
            if (key.startsWith("subclassIds")) {
                CompetitionSubclass competitionSubclass = JPA.em().find(CompetitionSubclass.class, new Long(dynamicData.data().get(key)));
                persistedData.subclasses.add(competitionSubclass);
            }
        }

        return redirect(routes.Users.list());
    }

    @Transactional
    public Result delete(Long userId) {
        User user = JPA.em().find(User.class, userId);
        if (user != null) {
            JPA.em().remove(user);
        }
        return redirect(routes.Users.list());
    }

    @Transactional(readOnly = true)
    public Result exportUsers() {
        return ok(Json.toJson(JPA.em().createQuery("SELECT u FROM User u ORDER BY cardId, id").getResultList()));
    }

    @Transactional
    public Result importUsers() {
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> users = body.getFile("users");
        if (users != null) {
            try {
                for (JsonNode item: Json.parse(new FileInputStream(users.getFile()))) {
                    User user = Json.fromJson(item, User.class);
                    user.id = new Long(0);
                    Query query = JPA.em().createQuery("SELECT u FROM User u WHERE firstName = :firstName AND lastName = :lastName AND cardId = :cardId");
                    query.setParameter("firstName", user.firstName).setParameter("lastName", user.lastName).setParameter("cardId", user.cardId);
                    if (query.getResultList().size() == 0) {
                        JPA.em().merge(user);
                    }
                }
            } catch (FileNotFoundException | RuntimeException ex) {
                flash().put("error", "message.error.unreadableinputfile");
            }
        } else {
            flash().put("error", "message.error.unreadableinputfile");
        }

        return redirect(routes.Users.list());
    }
}
