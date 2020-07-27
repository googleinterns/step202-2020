package com.google.sps.data;

public class Analysis {
  private int numReports;
  private List<String> top3;

  public Analysis(List<PoliceReport> reports) {
    this.numReports = reports.size();
    this.top3;
  }
}