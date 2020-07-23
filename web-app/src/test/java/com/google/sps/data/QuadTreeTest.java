package com.google.sps.data;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mockito;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

@RunWith(HierarchicalContextRunner.class)
@Category(JUnit4.class)
public class QuadTreeTest extends Mockito {

  private PoliceReport report1 = new PoliceReport(30.0, 15.0, "test1", 1234567); // NE
  private PoliceReport report2 = new PoliceReport(-30.0, 15.0, "test2", 1234567); // SE
  private PoliceReport report3 = new PoliceReport(30.0, -15.0, "test3", 1234567); // NW
  private PoliceReport report4 = new PoliceReport(-30.0, -15.0, "test4", 1234567); // SW
  private PoliceReport report5 = new PoliceReport(80.0, 45.0, "test5", 1234567); // NE

  ArrayList<PoliceReport> reportList = new ArrayList<PoliceReport>();

  @Before
  public void setUp() {
    reportList.add(report1);
    reportList.add(report2);
    reportList.add(report3);
    reportList.add(report4);
    reportList.add(report5);
  }

  public class TestInsert {
    QuadTree tree;

    private void customDepthTree(QuadTree tree, int depth) {
      Rectangle coordinates = new Rectangle(90.0, -180.0, -90.0, 180.0);
      tree.root = new QuadTree.Node(coordinates, new ArrayList<PoliceReport>(), depth);
    }

    @Before
    public void setUp() {
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

  // Query Tests
  @Nested
  class TestQuery {
    QuadTree simpleTree;

    @BeforeEach
    public void simpleTree() {
      simpleTree = new QuadTree();
      simpleTree.createTree();
      for (PoliceReport report : reportList) {
        simpleTree.insert(report);
      }
    }

    private List<String> reportsToCrimeType(List<PoliceReport> reports) {
      List<String> reportsCrimeType = new ArrayList<String>();
      for (PoliceReport report : reports) {
        reportsCrimeType.add(report.getCrimeType());
      }
      Collections.sort(reportsCrimeType);

      return reportsCrimeType;
    }

    @Test
    public void simpleQuery() throws IOException {
      Rectangle queryRange = new Rectangle(85.0, 15.0, 25.0, 50.0);
      List<PoliceReport> reportsInQueryRange = simpleTree.query(queryRange);
      Assert.assertEquals(2, reportsInQueryRange.size());
      
      List<String> reportsCrimeType = reportsToCrimeType(reportsInQueryRange);
      Assert.assertEquals("test1", reportsCrimeType.get(0));
      Assert.assertEquals("test5", reportsCrimeType.get(1));
    }

    @Test
    public void overlapTwoChildrenQuery() throws IOException {
      Rectangle queryRange = new Rectangle(40.0, -30.0, -40.0, -10.0);
      List<PoliceReport> reportsInQueryRange = simpleTree.query(queryRange);
      Assert.assertEquals(2, reportsInQueryRange.size());

      List<String> reportsCrimeType = reportsToCrimeType(reportsInQueryRange);
      Assert.assertEquals("test3", reportsCrimeType.get(0));
      Assert.assertEquals("test4", reportsCrimeType.get(1));
    }

    @Test
    public void noReportsInRange() throws IOException {
      Rectangle queryRange = new Rectangle(10.0, -10.0, -10.0, 10.0);
      List<PoliceReport> reportsInQueryRange = simpleTree.query(queryRange);
      Assert.assertEquals(0, reportsInQueryRange.size());
    }
  }

}
