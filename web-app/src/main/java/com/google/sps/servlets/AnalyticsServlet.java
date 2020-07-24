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

@WebServlet("/analytics")
public class AnalyticsServlet extends HttpServlet {

  private QuadTree reportsTree;

  @Override
  public void init() {
    reportsTree = new QuadTree();
    InputStream input = getServletContext().getResourceAsStream("/data/2019_12_london.json");
    /*Scanner s = new Scanner(input);

    for (int i = 0; i < 5; i++) {
      String line = s.nextLine();
      System.out.println(line);
    }*/

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new InputStreamReader(input));
    try {
      reader.beginArray();
      List<PoliceReport> reports = new ArrayList<PoliceReport>();
      while (reader.hasNext()) {
        PoliceReport report = gson.fromJson(reader, PoliceReport.class);
        System.out.println(report.getCrimeType());
        reports.add(report);
      }
      reader.endArray();
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}