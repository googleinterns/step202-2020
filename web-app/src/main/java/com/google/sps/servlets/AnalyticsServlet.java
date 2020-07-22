package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.sps.data.PoliceReport;
import com.google.sps.data.QuadTree;

@WebServlet("/analytics")
public class AnalyticsServlet extends HttpServlet {

  private QuadTree reportsTree;

  @Override
  public void init() {
    reportsTree = new QuadTree();
    reportsTree.createTree();

    
  }
}