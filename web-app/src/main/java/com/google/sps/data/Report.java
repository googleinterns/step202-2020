package com.google.sps.data;

public class Report {
  private final String title;
  private final double latitude;
  private final double longitude;
  private final long timestamp;
  private final String incidentType;
  private final String description;
  /** TODO (Ashley): Add image. */
}

public Report(String title, double latitude, double longitude, 
  long timestamp, String incidentType, String description) {
    this.title = title;
    this.latitude = latitude;
    this.longitude = longitude;
    this.timestamp = timestamp;
    this.incidentType = incidentType;
    this.description = description;
}