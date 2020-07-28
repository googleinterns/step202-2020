package com.google.sps.data;

public class Rectangle {
  private double topLeftLat;
  private double topLeftLng;
  private double bottomRightLat;
  private double bottomRightLng;

  Rectangle(double topLeftLat, double topLeftLng, double bottomRightLat, double bottomRightLng) {
    this.topLeftLat = topLeftLat;
    this.topLeftLng = topLeftLng;
    this.bottomRightLat = bottomRightLat;
    this.bottomRightLng = bottomRightLng;
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
}
