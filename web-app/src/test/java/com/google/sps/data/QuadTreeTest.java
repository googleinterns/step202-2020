package com.google.sps.data;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mockito;
import java.io.IOException;
import java.util.ArrayList;

@RunWith(JUnit4.class)
public class QuadTreeTest extends Mockito {

  private PoliceReport report1 = new PoliceReport(30.0, 15.0, "test1", 1234567); // NE
  private PoliceReport report2 = new PoliceReport(-30.0, 15.0, "test2", 1234567); // SE
  private PoliceReport report3 = new PoliceReport(30.0, -15.0, "test3", 1234567); // NW
  private PoliceReport report4 = new PoliceReport(-30.0, -15.0, "test4", 1234567); // SW
  private PoliceReport report5 = new PoliceReport(80.0, 45.0, "test5", 1234567); // NE

  ArrayList<PoliceReport> reportList = new ArrayList<PoliceReport>();
  QuadTree tree;

  private void customDepthTree(QuadTree tree, int depth) {
    Rectangle coordinates = new Rectangle(90.0, -180.0, -90.0, 180.0);
    tree.root = tree.new Node(coordinates, new ArrayList<PoliceReport>(), depth);
  }

  @Before
  public void setUp() {
    reportList.add(report1);
    reportList.add(report2);
    reportList.add(report3);
    reportList.add(report4);
    reportList.add(report5);

    tree = new QuadTree();
  }

  @Test
  public void reallocateReportsCorrectly() throws IOException {
    QuadTree.Node[] children = tree.reallocateReports(reportList, new Rectangle(90.0, -180.0, -90.0, 180.0), 0);
    // NW
    Assert.assertEquals("test3", children[0].reports.get(0).getCrimeType());
    // NE
    Assert.assertEquals("test1", children[1].reports.get(0).getCrimeType());
    Assert.assertEquals("test5", children[1].reports.get(1).getCrimeType());
    // SW
    Assert.assertEquals("test2", children[2].reports.get(0).getCrimeType());
    // SE
    Assert.assertEquals("test4", children[3].reports.get(0).getCrimeType());
  }

  @Test
  public void recursivelyCreateChildren() throws IOException {
    customDepthTree(tree, 5);
    for (int i = 0; i < 5; i++) {
      tree.insert(report3);
    }

    Assert.assertEquals(5, tree.root.children[0].children[2].children[1].numReports);
  }

  @Test
  public void stopAtMaxDepth() throws IOException {
    System.out.println("Start of function");
    customDepthTree(tree, QuadTree.maxDepth);
    System.out.println("custom depth tree");
    for (PoliceReport report : reportList) {
      tree.insert(report);
    }
    System.out.println("reports");
    // No new children should have been created
    Assert.assertTrue(tree.root.leaf);
    Assert.assertNull(tree.root.children);
  }
}
