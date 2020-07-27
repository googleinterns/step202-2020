package com.google.sps.data;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Analysis {
  private int numReports;
  private List<String> top3;
  private HashMap<String, Integer> crimeTypeCountMap;

  private void countCrimeType(List<PoliceReport> reports) {
    crimeTypeCountMap = new HashMap<String, Integer>();

    for (PoliceReport report : reports) {
      String crimeType = report.getCrimeType();
      Integer count = crimeTypeCountMap.get(crimeType);
      if (count == null) {
        crimeTypeCountMap.put(crimeType, 1);
      } else {
        crimeTypeCountMap.put(crimeType, count + 1);
      }
    }
  }

  private List<String> getMaxThreeTypes() {
  }

  public Analysis(List<PoliceReport> reports) {
    this.numReports = reports.size();
  }
}