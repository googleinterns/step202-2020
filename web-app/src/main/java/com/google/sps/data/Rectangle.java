package com.google.sps.data;

public class Rectangle {
  private double topLeftLat;
  private double topLeftLng;
  private double bottomRightLat;
  private double bottomRightLng;
  private double centerLat;
  private double centerLng;

  Rectangle(double topLeftLat, double topLeftLng, double bottomRightLat, double bottomRightLng) {
    this.topLeftLat = topLeftLat;
    this.topLeftLng = topLeftLng;
    this.bottomRightLat = bottomRightLat;
    this.bottomRightLng = bottomRightLng;
    this.centerLat = (topLeftLat + bottomRightLat) / 2;
    this.centerLng = (topLeftLng + bottomRightLng) / 2;
  }

  public double getTopLeftLat() {
    return topLeftLat;
  }

  public double getTopLeftLng() {
    return topLeftLng;
  }

  public double getBottomRightLat() {
    return bottomRightLat;
  }

  public double getBottomRightLng() {
    return bottomRightLng;
  }

  public boolean inRectangle(double lat, double lng) {
    return (lat <= topLeftLat && lat >= bottomRightLat && lng >= topLeftLng && lng <= bottomRightLng);
  }

  public Rectangle getNW() {
    return new Rectangle(topLeftLat, topLeftLng, centerLat, centerLng);
  }

  public Rectangle getNE() {
    return new Rectangle(topLeftLat, centerLng, centerLat, bottomRightLng);
  }

  public Rectangle getSE() {
    return new Rectangle(centerLat, centerLng, bottomRightLat, bottomRightLng);
  }

  public Rectangle getSW() {
    return new Rectangle(centerLat, topLeftLng, bottomRightLat, centerLng);
  }
}
