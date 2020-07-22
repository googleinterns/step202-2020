package com.google.sps.data;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Assert;
import org.mockito.Mockito;
import java.io.IOException;
import java.util.ArrayList;

@RunWith(JUnit4.class)
public class QuadTreeTest extends Mockito {

    @Test
    public void insertFiveReports() throws IOException {
        PoliceReport report1 = new PoliceReport(30.0, 15.0, "test", 1234567); // NE
        PoliceReport report2 = new PoliceReport(-30.0, 15.0, "test", 1234567); // SE
        PoliceReport report3 = new PoliceReport(30.0, -15.0, "test", 1234567); // NW
        PoliceReport report4 = new PoliceReport(-30.0, -15.0, "test", 1234567); // SW
        PoliceReport report5 = new PoliceReport(80.0, 45.0, "test", 1234567); // NE

        ArrayList<PoliceReport> reportList = new ArrayList<PoliceReport>();
        reportList.add(report1);
        reportList.add(report2);
        reportList.add(report3);
        reportList.add(report4);
        reportList.add(report5);

        QuadTree tree = new QuadTree();
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

}
