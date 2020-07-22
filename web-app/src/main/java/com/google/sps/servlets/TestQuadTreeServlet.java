package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.QuadTree;
import com.google.sps.data.PoliceReport;

@WebServlet("/quadtree")

public class TestQuadTreeServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    QuadTree tree = new QuadTree();
    PoliceReport report = new PoliceReport(30.0, 15.0, "test", 1234567);
    tree.createTree();
    tree.printTree();
    System.out.println("\n");
    tree.insert(report);
    tree.printTree();
  }
}
