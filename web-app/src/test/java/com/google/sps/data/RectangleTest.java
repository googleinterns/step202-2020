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

    Assert.assertTrue(outer.overlaps(inner));
    Assert.assertTrue(inner.overlaps(outer));
  }

}