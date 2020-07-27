package com.google.sps.data;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Analysis {
  private final int numReports;
  private final List<String> frequentTypes;
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

  private List<String> getTopNTypes(List<PoliceReport> reports, int n) {
    countCrimeType(reports);

    List<Entry<String, Integer>> crimeTypeFrequencyPairList = new ArrayList<>(crimeTypeCountMap.entrySet());
    crimeTypeFrequencyPairList.sort(Entry.comparingByValue());
    List<String>frequentTypes = new ArrayList<String>();

    int count = 0;
    while (count < n) {
      frequentTypes.add((crimeTypeFrequencyPairList.get(crimeTypeFrequencyPairList.size()-1-count)).getKey());
      count += 1;
    }

    return frequentTypes;
  }

  public Analysis(List<PoliceReport> reports, int n) {
    this.numReports = reports.size();
    this.frequentTypes = getTopNTypes(reports, n);
  }
}