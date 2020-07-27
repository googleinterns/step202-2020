package com.google.sps.data;

public class Rectangle {
  private Coordinates topLeft;
  private Coordinates bottomRight;
  private Coordinates center;

  public Rectangle(Coordinates topLeft, Coordinates bottomRight) {
    this.topLeft = topLeft;
    this.bottomRight = bottomRight;
    this.center = new Coordinates((topLeft.getLat() + bottomRight.getLat()) / 2,
        (topLeft.getLng() + bottomRight.getLng()) / 2);
  }

  public Coordinates getTopLeft() {
    return topLeft;
  }

  public double getTopLeftLat() {
    return topLeft.getLat();
  }

  public double getTopLeftLng() {
    return topLeft.getLng();
  }

  public Coordinates getBottomRight() {
    return bottomRight;
  }

  public double getBottomRightLat() {
    return bottomRight.getLat();
  }

  public double getBottomRightLng() {
    return bottomRight.getLng();
  }

  /**
   * Intersects: Return true if current rectangle intersects with the input, by
   * checking whether there is intersection in either longitude or latitude. Two
   * rectangles just sharing a single edge or point is not considered as
   * intersection.
   */
  public boolean intersects(Rectangle rect) {
    // No overlap in latitude
    if (topLeft.getLat() <= rect.getBottomRightLat() || rect.getTopLeftLat() <= bottomRight.getLat()) {
      return false;
    }

    // No overlap in longitue
    if (topLeft.getLng() >= rect.getBottomRightLng() || rect.getTopLeftLng() >= bottomRight.getLng()) {
      return false;
    }

    return true;
  }

  public boolean contains(double lat, double lng) {
    return (lat <= topLeft.getLat() && lat >= bottomRight.getLat() && lng >= topLeft.getLng()
        && lng <= bottomRight.getLng());
  }

  public Rectangle getNW() {
    return new Rectangle(topLeft, center);
  }

  public Rectangle getNE() {
    return new Rectangle(new Coordinates(topLeft.getLat(), center.getLng()),
        new Coordinates(center.getLat(), bottomRight.getLng()));
  }

  public Rectangle getSE() {
    return new Rectangle(center, bottomRight);
  }

  public Rectangle getSW() {
    return new Rectangle(new Coordinates(center.getLat(), topLeft.getLng()),
        new Coordinates(bottomRight.getLat(), center.getLng()));
  }
}
