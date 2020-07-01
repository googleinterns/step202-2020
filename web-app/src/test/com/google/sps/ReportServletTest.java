import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.io.*;
import javax.servlet.http.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ReportServletTest extends Mockito{

  @Test
  public void testReportServlet() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
  }

    @Test
    public void testServlet() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);    

        when(request.getParameter("title")).thenReturn("Test");
        when(request.getParameter("latitude")).thenReturn("1.11");
        when(request.getParameter("longitude")).thenReturn("10.26");
        when(request.getParameter("timestamp")).thenReturn("2017-06-01T08:30");
        when(request.getParameter("incidentType")).thenReturn("Theft");
        when(request.getParameter("description")).thenReturn("Sample request for testing");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new MyServlet().doPost(request, response);

        verify(request, atLeast(1)).getParameter("title"); // only if you want to verify username was called...
        writer.flush(); // it may not have been flushed yet...
        assertTrue(stringWriter.toString().contains("My expected string"));
    }
}