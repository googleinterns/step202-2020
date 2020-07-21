package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.PoliceReport;

@WebServlet("/analytics")
public class AnalyticsServlet extends HttpServlet {
  private Node root;

  private class Node {
    Double topLeftLat, topLeftLng, bottomRightLat, bottomRightLng;
    Node NW, NE, SE, SW;
    Boolean leaf = true;
    int depth;
    int numReports;
    ArrayList<PoliceReport> reports;

    Node(Double topLeftLat, Double topLeftLng, Double bottomRightLat, Double bottomRightLng,
        ArrayList<PoliceReport> reports, int depth) {
      this.topLeftLat = topLeftLat;
      this.topLeftLng = topLeftLng;
      this.bottomRightLat = bottomRightLat;
      this.bottomRightLng = bottomRightLng;
      this.depth = depth;
      this.numReports = reports.size();
      this.reports = reports;
    }
  }

  public Node createTree() {
    return new Node(90.0, -180.0, -90.0, 180.0, new ArrayList<PoliceReport>(), 0);
  }

}
