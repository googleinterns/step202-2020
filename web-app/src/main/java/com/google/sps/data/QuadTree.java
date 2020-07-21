package com.google.sps.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Deque;
import com.google.gson.Gson;
import com.google.sps.data.PoliceReport;
import com.google.sps.data.Rectangle;

public class QuadTree {
  private Node root;

  enum Direction {
    NW, NE, SE, SW
  }

  private class Node {
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
      if (node.leaf) {
        System.out.printf("%d", node.numReports);
      } else {
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
}
