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
        PoliceReport report1 = new PoliceReport(30.0, 15.0, "test", 1234567);
        PoliceReport report2 = new PoliceReport(-30.0, 15.0, "test", 1234567);
        PoliceReport report3 = new PoliceReport(30.0, -15.0, "test", 1234567);
        PoliceReport report4 = new PoliceReport(-30.0, -15.0, "test", 1234567);
        PoliceReport report5 = new PoliceReport(80.0, 45.0, "test", 1234567);

        ArrayList<PoliceReport> reportList = new ArrayList<PoliceReport>();
        reportList.add(report1);
        reportList.add(report2);
        reportList.add(report3);
        reportList.add(report4);
        reportList.add(report5);

        QuadTree tree = new QuadTree();
        QuadTree.Node[] children = tree.reallocateReports(reportList, new Rectangle(90.0, -180.0, -90.0, 180.0), 0);

        Assert.assertEquals(1, children[0].numReports);
        Assert.assertEquals(2, children[1].numReports);
        Assert.assertEquals(1, children[2].numReports);
        Assert.assertEquals(1, children[3].numReports);
    }

}
