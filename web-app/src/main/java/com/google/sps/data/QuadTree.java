package com.google.sps.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class QuadTree {
  private Node root;
  private final static int reportCapacity = 4;
  private final static int maxDepth = 8;

  enum Direction {
    NW, NE, SE, SW
  }

  private class Node {
    Rectangle coordinates;
    Node[] children;
    boolean leaf = true;
    int depth;
    int numReports;
    ArrayList<PoliceReport> reports;

    Node(Rectangle coordinates, ArrayList<PoliceReport> reports, int depth) {
      this.coordinates = coordinates;
      this.depth = depth;
      this.reports = reports;
      this.numReports = reports.size();
    }
  }

  QuadTree() {
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

  public void insert(PoliceReport report) {
    Node leafNode = findLeaf(root, report);
    leafNode.numReports += 1;
    leafNode.reports.add(report);
    // If max capacity has been exceeded, create child nodes
    if (leafNode.numReports > reportCapacity && leafNode.depth < maxDepth) {
      leafNode.children = reallocateReports(leafNode.reports, leafNode.coordinates, leafNode.depth);
      leafNode.leaf = false;
      leafNode.reports = null;
    }
  }

  // Find a leaf to insert the report in
  private Node findLeaf(Node currentNode, PoliceReport report) {
    double reportLat = report.getLat();
    double reportLng = report.getLng();

    while (!currentNode.leaf) {
      currentNode.numReports += 1;
      for (Node child : currentNode.children) {
        if (child.coordinates.inRectangle(reportLat, reportLng)) {
          currentNode = child;
          break;
        }
      }
    }

    return currentNode;
  }

  private Node[] reallocateReports(ArrayList<PoliceReport> reports, Rectangle coordinates, int depth) {
    Node[] children = new Node[4];

    int newDepth = depth + 1;
    children[Direction.NW.ordinal()] = new Node(coordinates.getNW(), new ArrayList<PoliceReport>(), newDepth);
    children[Direction.NE.ordinal()] = new Node(coordinates.getNE(), new ArrayList<PoliceReport>(), newDepth);
    children[Direction.SE.ordinal()] = new Node(coordinates.getSE(), new ArrayList<PoliceReport>(), newDepth);
    children[Direction.SW.ordinal()] = new Node(coordinates.getSW(), new ArrayList<PoliceReport>(), newDepth);

    for (PoliceReport report : reports) {
      for (Node childNode : children) {
        if (childNode.coordinates.inRectangle(report.getLat(), report.getLng())) {
          childNode.reports.add(report);
          break;
        }
      }
    }

    // Check if number of reports exceeds maximum
    for (Node childNode : children) {
      if (childNode.numReports > reportCapacity && childNode.depth < maxDepth) {
        childNode.children = reallocateReports(childNode.reports, childNode.coordinates, childNode.depth);
        childNode.leaf = false;
        childNode.reports = null;
      }
    }

    return children;
  }
}
