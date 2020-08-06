package com.google.sps.servlets;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.Entity;

@RunWith(JUnit4.class)
public class ReportServletTest extends Mockito {

  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
  }

  @Test
  public void testReportServlet() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("title")).thenReturn("Test");
    when(request.getParameter("latitude")).thenReturn("1.11");
    when(request.getParameter("longitude")).thenReturn("10.26");
    when(request.getParameter("timestamp")).thenReturn("2017-06-01T08:30");
    when(request.getParameter("incidentType")).thenReturn("Theft");
    when(request.getParameter("description")).thenReturn("Sample request for testing");
    when(request.getParameter("image")).thenReturn("no images");

    Entity testReport = ReportServlet.createReportEntity(request, response);

    Assert.assertEquals(testReport.getProperty("title"), "Test");
    Assert.assertEquals(testReport.getProperty("latitude"), 1.11);
    Assert.assertEquals(testReport.getProperty("longitude"), 10.26);
    Assert.assertEquals(testReport.getProperty("incidentType"), "Theft");
    Assert.assertEquals(testReport.getProperty("description"), "Sample request for testing");
    Assert.assertEquals(testReport.getProperty("timestamp"), 1496305800000L);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

}
