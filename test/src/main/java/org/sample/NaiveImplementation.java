package org.sample;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

public class NaiveImplementation {
  public static List<PoliceReport> search(PoliceReport[] reports, Coordinates[] waypoints) {
    List<PoliceReport> reportsNearLine = new ArrayList<PoliceReport>();

    for (PoliceReport report : reports) {

      int index = 0;
      while (index < waypoints.length - 1) {
        Coordinates start = waypoints[index];
        Coordinates end = waypoints[index + 1];
        if (Distance.distanceFromSegment(start, end, new Coordinates(report.getLat(), report.getLng())) < 0.0001) {
          reportsNearLine.add(report);
          break;
        }
        index += 1;
      }
    }
    return reportsNearLine;
  }
}
