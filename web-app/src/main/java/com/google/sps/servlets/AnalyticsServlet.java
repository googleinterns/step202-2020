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
import com.google.sps.data.Analysis;
import com.google.sps.data.Distance;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.lang.Math;

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

  private boolean isReportNearLine(PoliceReport report, Coordinates[] waypoints) {
    Coordinates reportLocation = new Coordinates(report.getLat(), report.getLng());
    int index = 0;
    while (index < waypoints.length - 1) {
      Coordinates start = waypoints[index];
      Coordinates end = waypoints[index + 1];

      if (Distance.distanceSquaredFromSegment(start, end, reportLocation) < 1e-8) {
        return true;
      }
      index += 1;
    }
    return false;
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

    if (waypoints.length < 2) {
      return;
    }

    Rectangle queryRange = Distance.getQueryRange(waypoints);
    List<PoliceReport> reportsInQueryRange = reportsTree.query(queryRange);
    
    List<PoliceReport> reportsNearLine = new ArrayList<PoliceReport>();

    for (PoliceReport report : reportsInQueryRange) {
      if (isReportNearLine(report, waypoints)) {
        reportsNearLine.add(report);
      }
    }

    Analysis analysis = new Analysis(reportsNearLine, 3);
    
    response.setContentType("application/json");
    String json = gson.toJson(analysis);
    response.getWriter().println(json);
  }
}
