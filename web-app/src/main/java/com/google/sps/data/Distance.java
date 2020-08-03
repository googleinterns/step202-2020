package com.google.sps.data;

import java.lang.Math;

public class Distance {
  public static Rectangle getQueryRange(Coordinates[] waypoints) {
    double minLongitude = 190.0;
    double maxLongitude = -190.0;
    double minLatitude = 100.0;
    double maxLatitude = -100.0;

    for (Coordinates waypoint : waypoints) {
      double latitude = waypoint.getLat();
      double longitude = waypoint.getLng();
      if (latitude > maxLatitude) {
        maxLatitude = latitude;
      }
      if (latitude < minLatitude) {
        minLatitude = latitude;
      }
      if (longitude > maxLongitude) {
        maxLongitude = longitude;
      }
      if (longitude < minLongitude) {
        minLongitude = longitude;
      }
    }

    return new Rectangle(new Coordinates(maxLatitude, minLongitude), new Coordinates(minLatitude, maxLongitude));
  }

  private static double square(double num) {
    return (num*num);
  }

  private static double distanceSquared(Coordinates start, Coordinates end) {
    return square(start.getLng()-end.getLng()) + square(start.getLat()-end.getLat());
  }

  public static double distanceFromSegment(Coordinates start, Coordinates end, Coordinates point) {
    double segmentDistanceSquared = distanceSquared(start, end);

    if (segmentDistanceSquared == 0) {
      return Math.sqrt(distanceSquared(start, point));
    }

    double projectionScale = 
      ((point.getLng() - start.getLng()) * (end.getLng()-start.getLng()) + (point.getLat() - start.getLat()) * (end.getLat()-start.getLat()))
      / segmentDistanceSquared;
    projectionScale = Math.max(0, Math.min(1, projectionScale));
    
    Coordinates projection = 
      new Coordinates(start.getLat() + projectionScale * (end.getLat() - start.getLat()), start.getLng() + projectionScale * (end.getLng() - start.getLng()));

    return Math.sqrt(distanceSquared(point, projection));
  }
}
