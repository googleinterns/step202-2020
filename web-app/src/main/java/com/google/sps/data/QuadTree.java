package com.google.sps.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Deque;

public class QuadTree {
  private Node root;
  private final static int reportCapacity = 4;
  private final static int maxDepth = 8;

  enum Direction {
    NW, NE, SE, SW
  }

  private class Node {
    Rectangle bounds;
    Node[] children;
    boolean leaf = true;
    int depth;
    int numReports;
    List<PoliceReport> reports;

    Node(Rectangle bounds, List<PoliceReport> reports, int depth) {
      this.bounds = bounds;
      this.depth = depth;
      this.reports = reports;
      this.numReports = reports.size();
    }

  }

  public QuadTree() {
    Rectangle bounds = new Rectangle(new Coordinates(90.0, -180.0), new Coordinates(-90.0, 180.0));
    root = new Node(bounds, new ArrayList<PoliceReport>(), 0);
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
      System.out.printf("(%f, %f), (%f, %f) ", node.bounds.getTopLeftLat(), node.bounds.getTopLeftLng(),
          node.bounds.getBottomRightLat(), node.bounds.getBottomRightLng());
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

    if (!node.bounds.intersects(range)) {
      return reports;
    }
    if (node.leaf) {
      for (PoliceReport report : node.reports) {
        if (range.contains(report.getLat(), report.getLng())) {
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
        if (child.bounds.contains(reportLat, reportLng)) {
          currentNode = child;
          break;
        }
      }
    }

    currentNode.numReports += 1;
    currentNode.reports.add(report);
    // If max capacity has been exceeded, create child nodes
    if (currentNode.numReports > reportCapacity && currentNode.depth < maxDepth) {
      currentNode.children = reallocateReports(currentNode.reports, currentNode.bounds, currentNode.depth);
      currentNode.leaf = false;
      currentNode.reports = null;
    }
  }

  private Node[] reallocateReports(List<PoliceReport> reports, Rectangle bounds, int depth) {
    Node[] children = new Node[4];

    for (Direction direction : Direction.values()) {
      Rectangle newbounds;
      ArrayList<PoliceReport> newReports = new ArrayList<PoliceReport>();
      int newDepth = depth + 1;

      switch (direction) {
        case NW:
          newbounds = bounds.getNW();
          break;
        case NE:
          newbounds = bounds.getNE();
          break;
        case SE:
          newbounds = bounds.getSE();
          break;
        case SW:
          newbounds = bounds.getSW();
          break;
        default:
          System.out.println("Unexpected case in switch statement");
          return children;
      }

      for (PoliceReport report : reports) {
        if (newbounds.contains(report.getLat(), report.getLng())) {
          newReports.add(report);
        }
      }

      Node childNode = new Node(newbounds, newReports, newDepth);
      // Check if number of reports exceeds maximum
      if (childNode.numReports > reportCapacity && newDepth < maxDepth) {
        childNode.children = reallocateReports(newReports, newbounds, newDepth);
        childNode.leaf = false;
        childNode.reports = null;
      }

      children[direction.ordinal()] = childNode;
    }

    return children;
  }
}
