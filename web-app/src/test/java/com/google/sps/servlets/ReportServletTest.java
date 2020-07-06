package com.google.sps;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.Entity;

@RunWith(JUnit4.class)
public class ReportServletTest extends Mockito {
  
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

    Entity testReport = createReportEntity(request, response);

    
  }

}