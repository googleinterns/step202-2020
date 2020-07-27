package com.google.sps.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.sps.data.PoliceReport;
import com.google.sps.data.Rectangle;
import com.google.sps.data.QuadTree;
import com.google.sps.data.Coordinates;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@WebServlet("/analytics")
public class AnalyticsServlet extends HttpServlet {

  private QuadTree reportsTree;

  private List<PoliceReport> jsonToPoliceReportList(String path) {
    InputStream input = getServletContext().getResourceAsStream(path);
    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new InputStreamReader(input));
    List<PoliceReport> reports = new ArrayList<PoliceReport>();

    try {
      reader.beginArray();
      while (reader.hasNext()) {
        PoliceReport report = gson.fromJson(reader, PoliceReport.class);
        reports.add(report);
      }
      reader.endArray();
    } catch (Exception e) {
      // If Json file is corrupted, just exclude that file from analysis
      return new ArrayList<PoliceReport>();
    }
    return reports;
  }

  @Override
  public void init() {
    reportsTree = new QuadTree();
    Set<String> paths = getServletContext().getResourcePaths("/data");

    for (String path : paths) {
      List<PoliceReport> reports = jsonToPoliceReportList(path);
      for (PoliceReport report : reports) {
        reportsTree.insert(report);
      }
    }
  }

  private Rectangle getQueryRange(Coordinates[] waypoints) {
    double minLongitude = 190.0;
    double maxLongitude = -190.0;
    double minLatitude = 100.0;
    double maxLatitude = -100.0;

    for (Coordinates waypoint : waypoints) {
      double latitude = waypoint.getLat();
      double longitude = waypoint.getLng();
      if (latitude > maxLatitude) {
        maxLatitude = latitude;
      }
      if (latitude < minLatitude) {
        minLatitude = latitude;
      }
      if (longitude > maxLongitude) {
        maxLongitude = longitude;
      }
      if (longitude < minLongitude) {
        minLongitude = longitude;
      }
    }

    return new Rectangle(new Coordinates(maxLatitude, minLongitude), new Coordinates(minLatitude, maxLongitude));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Coordinates[] waypoints;
    Gson gson = new Gson();

    try {
      JsonReader reader = new JsonReader(request.getReader());
      waypoints = new Gson().fromJson(reader, Coordinates[].class);
    } catch (Exception e) {
      System.out.println(e);
      return;
    }

    Rectangle queryRange = getQueryRange(waypoints);
    
  }
}
