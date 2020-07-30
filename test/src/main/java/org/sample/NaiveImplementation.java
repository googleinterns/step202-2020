package org.sample;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

public class NaiveImplementation {
  private double square(double num) {
    return (num * num);
  }

  private double distanceSquared(Coordinates start, Coordinates end) {
    return square(start.getLng() - end.getLng()) + square(start.getLat() - end.getLat());
  }

  private double distanceFromSegment(Coordinates start, Coordinates end, Coordinates point) {
    double segmentDistanceSquared = distanceSquared(start, end);

    if (segmentDistanceSquared == 0) {
      return Math.sqrt(distanceSquared(start, point));
    }

    double projectionScale = ((point.getLng() - start.getLng()) * (end.getLng() - start.getLng())
        + (point.getLat() - start.getLat()) * (end.getLat() - start.getLat())) / segmentDistanceSquared;
    projectionScale = Math.max(0, Math.min(1, projectionScale));

    Coordinates projection = new Coordinates(start.getLat() + projectionScale * (end.getLat() - start.getLat()),
        start.getLng() + projectionScale * (end.getLng() - start.getLng()));

    return Math.sqrt(distanceSquared(point, projection));
  }

  public List<PoliceReport> search(PoliceReport[] reports, Coordinates[] waypoints) {
    List<PoliceReport> reportsNearLine = new ArrayList<PoliceReport>();

    for (PoliceReport report : reports) {

      int index = 0;
      while (index < waypoints.length - 1) {
        Coordinates start = waypoints[index];
        Coordinates end = waypoints[index + 1];
        if (distanceFromSegment(start, end, new Coordinates(report.getLat(), report.getLng())) < 0.0001) {
          reportsNearLine.add(report);
          break;
        }
        index += 1;
      }
    }
    return reportsNearLine;
  }

}
