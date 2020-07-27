package com.google.sps.data;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class AnalysisTest {

  @Test
  public void top2FrequentTypes() {
    List<PoliceReport> reportList = new ArrayList<PoliceReport>();
    String[] crimeTypes = {"theft", "theft", "arson", "drugs", "drugs", "theft"};
    for (String crimeType : crimeTypes) {
      reportList.add(new PoliceReport(0.0, 0.0, crimeType, 123));
    }

    Analysis analysis = new Analysis(reportList, 2);
    Assert.assertEquals(6, analysis.getNumReports());
    Assert.assertEquals(2, analysis.getFrequentTypes().size());
    Assert.assertEquals("theft", analysis.getFrequentTypes().get(0));
    Assert.assertEquals("drugs", analysis.getFrequentTypes().get(1));
  }

  @Test
  public void top0FrequentTypes() {
    List<PoliceReport> reportList = new ArrayList<PoliceReport>();
    String[] crimeTypes = {"theft", "theft"};
    for (String crimeType : crimeTypes) {
      reportList.add(new PoliceReport(0.0, 0.0, crimeType, 123));
    }

    Analysis analysis = new Analysis(reportList, 0);
    Assert.assertEquals(2, analysis.getNumReports());
    Assert.assertEquals(0, analysis.getFrequentTypes().size());
  }

  @Test
  public void requestMoreThanExistingTypes() {
    // When it asks for top 4 crimeTypes, and there are only 2 types among the reports,
    // It should return both of the 2 types without raising error. 
    List<PoliceReport> reportList = new ArrayList<PoliceReport>();
    String[] crimeTypes = {"theft", "theft", "arson"};
    for (String crimeType : crimeTypes) {
      reportList.add(new PoliceReport(0.0, 0.0, crimeType, 123));
    }

    Analysis analysis = new Analysis(reportList, 4);
    Assert.assertEquals(3, analysis.getNumReports());
    Assert.assertEquals(2, analysis.getFrequentTypes().size());
    Assert.assertEquals("theft", analysis.getFrequentTypes().get(0));
    Assert.assertEquals("arson", analysis.getFrequentTypes().get(1));
  }
}