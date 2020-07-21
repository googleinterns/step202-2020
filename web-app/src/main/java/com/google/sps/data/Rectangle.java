package com.google.sps.data;

public class Rectangle {
  double topLeftLat;
  double topLeftLng;
  double bottomRightLat;
  double bottomRightLng;

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

  boolean overlaps(Rectangle rect) {
    // No overlap in latitude
    if (topLeftLat >= rect.bottomRightLat || rect.topLeftLat >= bottomRightLat) {
      return false;
    }
  
    // No overlap in longitue  
    if (topLeftLng <= rect.bottomRightLng || rect.topLeftLng <= bottomRightLng) { 
      return false; 
    }

    return true;
  }
}
