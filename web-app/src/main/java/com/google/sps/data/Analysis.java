package com.google.sps.data;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.lang.Math;

public class Analysis {

  public Analysis(List<PoliceReport> reports, int n) {
    this.numReports = reports.size();
    this.frequentTypes = getTopNTypes(reports, n);
  }

  private final int numReports;
  private final List<String> frequentTypes;

  private HashMap<String, Integer> countCrimeType(List<PoliceReport> reports) {
    HashMap<String, Integer> crimeTypeCountMap = new HashMap<String, Integer>();

    for (PoliceReport report : reports) {
      String crimeType = report.getCrimeType();
      Integer count = crimeTypeCountMap.get(crimeType);
      if (count == null) {
        crimeTypeCountMap.put(crimeType, 1);
      } else {
        crimeTypeCountMap.put(crimeType, count + 1);
      }
    }
    
    return crimeTypeCountMap;
  }

  private List<String> getTopNTypes(List<PoliceReport> reports, int n) {
    HashMap<String, Integer> crimeTypeCountMap = countCrimeType(reports);

    List<Entry<String, Integer>> crimeTypeFrequencyPairList = new ArrayList<>(crimeTypeCountMap.entrySet());
    crimeTypeFrequencyPairList.sort(Entry.comparingByValue());
    List<String>frequentTypes = new ArrayList<String>();

    for (int i = 0; i < Math.min(crimeTypeFrequencyPairList.size(), n); i++) {
      frequentTypes.add((crimeTypeFrequencyPairList.get(crimeTypeFrequencyPairList.size()-1-i)).getKey());
    }

    return frequentTypes;
  }

  public int getNumReports() {
    return this.numReports;
  }

  public List<String> getFrequentTypes() {
    return this.frequentTypes;
  }
}
