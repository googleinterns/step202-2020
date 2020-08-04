package org.sample;

import java.util.ArrayList;
import java.util.List;

public class QuadtreeImplementation {
  public static List<PoliceReport> search(QuadTree reportsTree, Coordinates[] waypoints) {
    Rectangle queryRange = Distance.getQueryRange(waypoints);
    List<PoliceReport> reportsInQueryRange = reportsTree.query(queryRange);
    List<PoliceReport> reportsNearLine = new ArrayList<PoliceReport>();

    for (PoliceReport report : reportsInQueryRange) {
      Coordinates reportLocation = new Coordinates(report.getLat(), report.getLng());

      int index = 0;
      while (index < waypoints.length - 1) {
        Coordinates start = waypoints[index];
        Coordinates end = waypoints[index + 1];

        if (Distance.distanceFromSegment(start, end, reportLocation) < 0.0001) {
          reportsNearLine.add(report);
          break;
        }
        index += 1;
      }
    }

    return reportsNearLine;
  }
}
