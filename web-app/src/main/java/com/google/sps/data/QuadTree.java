package com.google.sps.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Deque;
import com.google.sps.data.PoliceReport;
import com.google.sps.data.Rectangle;

public class QuadTree {
  private Node root;
  private final int reportCapacity = 4;
  private final int maxDepth = 8;

  enum Direction {
    NW, NE, SE, SW
  }

  public class Node {
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
      // Size to accomodate space for one ext
      this.children = new Node[4];
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
    if (!node.coordinates.overlaps(range)) {
      // Don't traverse further down the tree
      return new ArrayList<PoliceReport>();
    }
    if (node.leaf) {
      return node.reports;
    }
    return node.children.stream()
      .flatMap(childNode -> findAllReports(range, childNode))
      .collect(Collectors.toList());
  } 

  public void insert(PoliceReport report) {
    Node currentNode = root;
    double reportLat = report.getLat();
    double reportLng = report.getLng();

    // Find a leaf to insert the report in
    while (!currentNode.leaf) {
      currentNode.numReports += 1;
      // Check which child the report belongs to
      for (Direction direction : Direction.values()) {
        Node childNode = currentNode.children[direction.ordinal()];
        if (childNode.coordinates.inRectangle(reportLat, reportLng)) {
          currentNode = childNode;
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

  public Node[] reallocateReports(ArrayList<PoliceReport> reports, Rectangle coordinates, int depth) {
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
      if (childNode.numReports > reportCapacity) {
        childNode.children = reallocateReports(newReports, newCoordinates, newDepth);
        childNode.leaf = false;
        childNode.reports = null;
      }

      children[direction.ordinal()] = childNode;
    }

    System.out.println(children);
    return children;
  }
}
