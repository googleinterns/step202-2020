package com.google.sps.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import com.google.gson.Gson;
import com.google.sps.data.PoliceReport;

public class QuadTree {
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
      this.reports = reports;
      this.numReports = reports.size();
    }
  }

  public void createTree() {
    root = new Node(90.0, -180.0, -90.0, 180.0, new ArrayList<PoliceReport>(), 0);
  }

  public void printTree() {
    int currentLevel = root.depth;
    Deque<Node> nodesToPrint = new ArrayDeque<Node>();
    nodesToPrint.add(root);

    while (!nodesToPrint.isEmpty()) {
      Node node = nodesToPrint.pollFirst();
      if (currentLevel != node.depth) {
        System.out.printf("%n");
        currentLevel = node.depth;
      }
      System.out.printf("(%f, %f), (%f, %f) ", node.topLeftLat, node.topLeftLng, node.bottomRightLat,
          node.bottomRightLng);
      if (node.leaf) {
        System.out.printf("%d", node.numReports);
      } else {
        nodesToPrint.push(node.NW);
        nodesToPrint.push(node.NE);
        nodesToPrint.push(node.SE);
        nodesToPrint.push(node.SW);
      }
      System.out.printf(" | ");
    }
    System.out.printf("%n");
  }

}
