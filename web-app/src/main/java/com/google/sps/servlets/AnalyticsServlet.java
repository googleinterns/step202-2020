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
import com.google.sps.data.QuadTree;
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
      return new ArrayList<PoliceReport>();
    }
    return reports;
  }

  @Override
  public void init() {
    reportsTree = new QuadTree();
    Set paths = getServletContext().getResourcePaths("/data");
    System.out.println(paths);
    
  }
}