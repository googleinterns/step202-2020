package com.google.sps.data;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

@RunWith(JUnit4.class)
public class RectangleTest {

  @Test
  public void containsOtherRectangle() {
    Rectangle outer = new Rectangle(90.0, -180.0, -90.0, 180.0);
    Rectangle inner = new Rectangle(70.0, 30.0, 20.0, 50.0);

    Assert.assertTrue(outer.intersects(inner));
    Assert.assertTrue(inner.intersects(outer));
  }

  @Test
  public void overlapRectangle() {
    Rectangle r1 = new Rectangle(30.0, -10.0, -10.0, 30.0);
    Rectangle r2 = new Rectangle(15.0, 20.0, -40.0, 50.0);

    Assert.assertTrue(r1.intersects(r2));
    Assert.assertTrue(r2.intersects(r1));
  }

  @Test
  public void disjointRectangles() {
    Rectangle r1 = new Rectangle(80.0, -20.0, 30.0, -10.0);
    Rectangle r2 = new Rectangle(80.0, 30.0, 30.0, 50.0);

    Assert.assertFalse(r1.intersects(r2));
    Assert.assertFalse(r2.intersects(r1));
  }
}
