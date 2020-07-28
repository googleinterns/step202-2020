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

  QuadTree() {
    Rectangle bounds = new Rectangle(90.0, -180.0, -90.0, 180.0);
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
    Node leafNode = findLeaf(root, report);
    leafNode.numReports += 1;
    leafNode.reports.add(report);
    // If max capacity has been exceeded, create child nodes
    if (leafNode.numReports > reportCapacity && leafNode.depth < maxDepth) {
      leafNode.children = reallocateReports(leafNode.reports, leafNode.bounds, leafNode.depth);
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
        if (child.bounds.contains(reportLat, reportLng)) {
          currentNode = child;
          break;
        }
      }
    }

    return currentNode;
  }


  private Node[] reallocateReports(List<PoliceReport> reports, Rectangle bounds, int depth) {
    Node[] children = new Node[4];

    int newDepth = depth + 1;
    children[Direction.NW.ordinal()] = new Node(bounds.getNW(), new ArrayList<PoliceReport>(), newDepth);
    children[Direction.NE.ordinal()] = new Node(bounds.getNE(), new ArrayList<PoliceReport>(), newDepth);
    children[Direction.SE.ordinal()] = new Node(bounds.getSE(), new ArrayList<PoliceReport>(), newDepth);
    children[Direction.SW.ordinal()] = new Node(bounds.getSW(), new ArrayList<PoliceReport>(), newDepth);

    for (PoliceReport report : reports) {
      for (Node childNode : children) {
        if (childNode.bounds.contains(report.getLat(), report.getLng())) {
          childNode.reports.add(report);
          break;
        }
      }
    }

    // Check if number of reports exceeds maximum
    for (Node childNode : children) {
      if (childNode.numReports > reportCapacity && childNode.depth < maxDepth) {
        childNode.children = reallocateReports(childNode.reports, childNode.bounds, childNode.depth);
        childNode.leaf = false;
        childNode.reports = null;
      }
    }

    return children;
  }
}
