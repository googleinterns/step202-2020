package com.google.sps.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Deque;

public class QuadTree {
  public Node root;
  private final int reportCapacity = 4;
  public final static int maxDepth = 8;

  enum Direction {
    NW, NE, SE, SW
  }

  public static class Node {
    Rectangle coordinates;
    Node[] children;
    boolean leaf = true;
    int depth;
    int numReports;
    List<PoliceReport> reports;

    Node(Rectangle coordinates, List<PoliceReport> reports, int depth) {
      this.coordinates = coordinates;
      this.depth = depth;
      this.reports = reports;
      this.numReports = reports.size();
    }

  }

  public void createTree() {
    Rectangle coordinates = new Rectangle(90.0, -180.0, -90.0, 180.0);
    root = new Node(coordinates, new ArrayList<PoliceReport>(), 0);
  }

  public void printTree() {
    int currentLevel = root.depth;
    Deque<Node> nodesToPrint = new ArrayDeque<Node>();
    nodesToPrint.push(root);

    while (!nodesToPrint.isEmpty()) {
      Node node = nodesToPrint.pollFirst();
      if (currentLevel != node.depth) {
        System.out.printf("%n");
        currentLevel = node.depth;
      }
      System.out.printf("(%f, %f), (%f, %f) ", node.coordinates.getTopLeftLat(), node.coordinates.getTopLeftLng(),
          node.coordinates.getBottomRightLat(), node.coordinates.getBottomRightLng());
      System.out.printf("%d", node.numReports);
      if (!node.leaf) {
        for (Direction direction : Direction.values()) {
          nodesToPrint.push(node.children[direction.ordinal()]);
        }
      }
      System.out.printf(" | ");
    }
    System.out.printf("%n");
  }

  public List<PoliceReport> query(Rectangle range) {
    return findAllReports(range, root);
  }

  private List<PoliceReport> findAllReports(Rectangle range, Node node) {
    List<PoliceReport> reports = new ArrayList<PoliceReport>();

    if (!node.coordinates.overlaps(range)) {
      return reports;
    }
    if (node.leaf) {
      for (PoliceReport report : node.reports) {
        if (range.inRectangle(report.getLat(), report.getLng())) {
          reports.add(report);
        }
      }
      return reports;
    }

    for (Node child : node.children) {
      reports.addAll(findAllReports(range, child));
    }
    return reports;
  } 

  public void insert(PoliceReport report) {
    Node currentNode = root;
    double reportLat = report.getLat();
    double reportLng = report.getLng();

    // Find a leaf to insert the report in
    while (!currentNode.leaf) {
      currentNode.numReports += 1;
      for (Node child : currentNode.children) {
        if (child.coordinates.inRectangle(reportLat, reportLng)) {
          currentNode = child;
          break;
        }
      }
    }

    currentNode.numReports += 1;
    currentNode.reports.add(report);
    // If max capacity has been exceeded, create child nodes
    if (currentNode.numReports > reportCapacity && currentNode.depth < maxDepth) {
      currentNode.children = reallocateReports(currentNode.reports, currentNode.coordinates, currentNode.depth);
      currentNode.leaf = false;
      currentNode.reports = null;
    }
  }

  public Node[] reallocateReports(List<PoliceReport> reports, Rectangle coordinates, int depth) {
    Node[] children = new Node[4];

    for (Direction direction : Direction.values()) {
      Rectangle newCoordinates;
      ArrayList<PoliceReport> newReports = new ArrayList<PoliceReport>();
      int newDepth = depth + 1;

      switch (direction) {
        case NW:
          newCoordinates = coordinates.getNW();
          break;
        case NE:
          newCoordinates = coordinates.getNE();
          break;
        case SE:
          newCoordinates = coordinates.getSE();
          break;
        case SW:
          newCoordinates = coordinates.getSW();
          break;
        default:
          System.out.println("Unexpected case in switch statement");
          return children;
      }

      for (PoliceReport report : reports) {
        if (newCoordinates.inRectangle(report.getLat(), report.getLng())) {
          newReports.add(report);
        }
      }

      Node childNode = new Node(newCoordinates, newReports, newDepth);
      // Check if number of reports exceeds maximum
      if (childNode.numReports > reportCapacity && newDepth < maxDepth) {
        childNode.children = reallocateReports(newReports, newCoordinates, newDepth);
        childNode.leaf = false;
        childNode.reports = null;
      }

      children[direction.ordinal()] = childNode;
    }

    return children;
  }
}
