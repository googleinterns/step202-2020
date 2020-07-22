package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.QuadTree;
import com.google.sps.data.PoliceReport;

@WebServlet("/quadtree")

public class TestQuadTreeServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    QuadTree tree = new QuadTree();
    PoliceReport report0 = new PoliceReport(30.0, 15.0, "test", 1234567);
    PoliceReport report1 = new PoliceReport(35.0, 30.0, "test", 1234567);
    PoliceReport report2 = new PoliceReport(34.0, 31.0, "test", 1234567);
    PoliceReport report3 = new PoliceReport(85.0, 31.0, "test", 1234567);
    PoliceReport report4 = new PoliceReport(-85.0, 31.0, "test", 1234567);
    tree.createTree();
    tree.insert(report0);
    tree.insert(report1);
    tree.insert(report2);
    tree.insert(report3);
    tree.insert(report4);
    tree.printTree();
  }
}
