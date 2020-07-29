package com.google.sps.data;

public class PoliceReport {
  private final double latitude;
  private final double longitude;
  private final String crimeType;
  private final long timestamp;

  public PoliceReport(double latitude, double longitude, String crimeType, long timestamp) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.crimeType = crimeType;
    this.timestamp = timestamp;
  }

  public double getLat() {
    return latitude;
  }

  public double getLng() {
    return longitude;
  }

  public String getCrimeType() {
    return crimeType;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
