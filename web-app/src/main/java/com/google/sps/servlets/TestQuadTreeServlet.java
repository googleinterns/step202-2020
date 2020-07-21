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

@WebServlet("/quadtree")

public class TestQuadTreeServlet extends HttpServlet {
  
  @Override 
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    QuadTree.printTree(QuadTree.createTree());
  }
}