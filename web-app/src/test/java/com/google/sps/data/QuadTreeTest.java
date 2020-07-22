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

  private PoliceReport report1 = new PoliceReport(30.0, 15.0, "test", 1234567); // NE
  private PoliceReport report2 = new PoliceReport(-30.0, 15.0, "test", 1234567); // SE
  private PoliceReport report3 = new PoliceReport(30.0, -15.0, "test", 1234567); // NW
  private PoliceReport report4 = new PoliceReport(-30.0, -15.0, "test", 1234567); // SW
  private PoliceReport report5 = new PoliceReport(80.0, 45.0, "test", 1234567); // NE

  ArrayList<PoliceReport> reportList = new ArrayList<PoliceReport>();
  QuadTree tree;

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
    Assert.assertEquals(report3.getLat(), children[0].reports.get(0).getLat(), 0.0001);
    Assert.assertEquals(report3.getLng(), children[0].reports.get(0).getLng(), 0.0001);
    // NE
    Assert.assertEquals(report1.getLat(), children[1].reports.get(0).getLat(), 0.0001);
    Assert.assertEquals(report1.getLng(), children[1].reports.get(0).getLng(), 0.0001);
    Assert.assertEquals(report5.getLat(), children[1].reports.get(1).getLat(), 0.0001);
    Assert.assertEquals(report5.getLng(), children[1].reports.get(1).getLng(), 0.0001);
    // SW
    Assert.assertEquals(report2.getLat(), children[2].reports.get(0).getLat(), 0.0001);
    Assert.assertEquals(report2.getLng(), children[2].reports.get(0).getLng(), 0.0001);
    // SE
    Assert.assertEquals(report4.getLat(), children[3].reports.get(0).getLat(), 0.0001);
    Assert.assertEquals(report4.getLng(), children[3].reports.get(0).getLng(), 0.0001);
  }

  @Test
  public void recursivelyCreateChildren() throws IOException {
    tree.customDepthTree(5);
    for (int i = 0; i < 5; i++) {
      tree.insert(report3);
    }

    Assert.assertEquals(5, tree.root.children[0].children[2].children[1].numReports);
  }

  @Test
  public void stopAtMaxDepth() throws IOException {
    tree.customDepthTree(tree.maxDepth);

    for (PoliceReport report : reportList) {
      tree.insert(report);
    }
    // No new children should have been created
    Assert.assertTrue(tree.root.leaf);
    for (QuadTree.Node element : tree.root.children) {
      Assert.assertNull(element);
    }
  }

}