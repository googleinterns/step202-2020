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

  double getTopLeftLat() {
    return topLeftLat;
  }

  double getTopLeftLng() {
    return topLeftLng;
  }

  double getBottomRightLat() {
    return bottomRightLat;
  }

  double getBottomRightLng() {
    return bottomRightLng;
  }
}
