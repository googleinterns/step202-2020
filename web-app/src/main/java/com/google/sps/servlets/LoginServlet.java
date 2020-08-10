package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  private static class LoginStatus {
    private String url;
    private Boolean loggedIn;

    private LoginStatus(String url, Boolean loggedIn) {
      this.url = url;
      this.loggedIn = loggedIn;
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    LoginStatus loginStatus;
    if (!userService.isUserLoggedIn()) {
      loginStatus = new LoginStatus(userService.createLoginURL("/"), false);
    } else {
      loginStatus = new LoginStatus(userService.createLogoutURL("/"), true);
    }

    Gson gson = new Gson();
    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(loginStatus));
  }
}
