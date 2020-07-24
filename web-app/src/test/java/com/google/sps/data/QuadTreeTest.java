package com.google.sps.data;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
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

  List<PoliceReport> reportList = new ArrayList<PoliceReport>();

  private List<String> reportsToCrimeType(List<PoliceReport> reports) {
    List<String> reportsCrimeType = new ArrayList<String>();
    for (PoliceReport report : reports) {
      reportsCrimeType.add(report.getCrimeType());
    }
    Collections.sort(reportsCrimeType);

    return reportsCrimeType;
  }

  public class simpleTreeTests {
    QuadTree simpleTree;

    @Before
    public void simpleTree() {
      reportList.add(report1);
      reportList.add(report2);
      reportList.add(report3);
      reportList.add(report4);
      reportList.add(report5);

      simpleTree = new QuadTree();
      for (PoliceReport report : reportList) {
        simpleTree.insert(report);
      }
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

  @Test
  public void duplicateLocations() throws IOException {
    QuadTree tree = new QuadTree();
    Rectangle queryRange = new Rectangle(-20.0, -20.0, -40.0, -10.0);
    for (int i = 0; i < 5; i++) {
      tree.insert(report4);
    }
    Assert.assertEquals(5, tree.query(queryRange).size());
  }

  @Test
  public void reportOnBoundaryLine() throws IOException {
    QuadTree tree = new QuadTree();
    PoliceReport report = new PoliceReport(0, 0, "corner case", 1234567);
    Rectangle NWQuery = new Rectangle(90.0, -180.0, 0.0, 0.0);
    Rectangle NEQuery = new Rectangle(90.0, 0.0, 0.0, 180.0);
    Rectangle SEQuery = new Rectangle(0.0, 0.0, -90.0, 180.0);
    Rectangle SWQuery = new Rectangle(0.0, -180.0, -90.0, 0.0);
    Rectangle fullQuery = new Rectangle(90.0, -180.0, -90.0, 180.0);

    tree.insert(report);
    // Corner reports should appear in every query
    Assert.assertEquals(1, tree.query(NWQuery).size());
    Assert.assertEquals(1, tree.query(NEQuery).size());
    Assert.assertEquals(1, tree.query(SEQuery).size());
    Assert.assertEquals(1, tree.query(SWQuery).size());
    // Corner reports should only appear once
    Assert.assertEquals(1, tree.query(fullQuery).size());
  }

}
