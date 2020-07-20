package com.google.sps.data;

public class PoliceReport {
  private final double latitude;
  private final double longitude;
  private final String crimeType;
  private final long timeStamp;

  public PoliceReport(double latitude, double longitude, String crimeType, long timeStamp) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.crimeType = crimeType;
    this.timeStamp = timeStamp;
  }
}