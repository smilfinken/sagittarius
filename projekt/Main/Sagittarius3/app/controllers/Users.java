package controllers;

import models.User;
import views.html.useredit;
import views.html.userlist;

import play.data.FormFactory;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

import java.util.ArrayList;

public class Users extends Controller {
    @Inject
    private FormFactory formFactory;

    @Transactional(readOnly = true)
    public Result list() {
        ArrayList<User> users = (ArrayList<User>)JPA.em().createQuery("SELECT c FROM User c", User.class).getResultList();
        return ok(userlist.render(users));
    }

    @Transactional
    public Result edit(Long userId) {
        if (userId != 0) {
            return ok(useredit.render(JPA.em().find(User.class, userId)));
        } else {
            return ok(useredit.render(new User()));
        }
    }

    @Transactional
    public Result save() {
        User requestData = formFactory.form(User.class).bindFromRequest().get();
        User persistedData = JPA.em().find(User.class, requestData.id);

        if (persistedData != null) {
            persistedData.copyValues(requestData);
        } else {
            JPA.em().merge(requestData);
        }

        return redirect(routes.Users.list());
    }
}
